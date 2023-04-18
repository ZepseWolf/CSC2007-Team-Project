import * as dotenv from 'dotenv'
import { applicationDefault, initializeApp } from 'firebase-admin/app'
import { getFirestore } from 'firebase-admin/firestore'
import { getMessaging } from 'firebase-admin/messaging'

// Load environment variables
dotenv.config()

// Firebase constants
const app = initializeApp({ credential: applicationDefault() })
const db = getFirestore(app)
const messaging = getMessaging(app)

// Script that sends push notifications to users
// Poll every 5 seconds
Promise.resolve().then(async () => {
  while (true) {
    try {
      console.log('Checking for pending notifications...')

      // Notify any equipment or schedule at least one day before the event
      const sampleEndoscopes = await db
        .collection('endoscopes')
        .where('hasNotifySample', '==', false)
        .where('upcomingSample', '<=', new Date(Date.now() + 86400000))
        .get()
      const repairEndoscopes = await db
        .collection('endoscopes')
        .where('hasNotifyRepair', '==', false)
        .where('upcomingRepair', '<=', new Date(Date.now() + 86400000))
        .get()
      const sampleWashers = await db
        .collection('washers')
        .where('hasNotifySample', '==', false)
        .where('upcomingSample', '<=', new Date(Date.now() + 86400000))
        .get()
      const repairWashers = await db
        .collection('washers')
        .where('hasNotifyRepair', '==', false)
        .where('upcomingRepair', '<=', new Date(Date.now() + 86400000))
        .get()

      const startEvents = await db
        .collection('events')
        .where('hasNotifyStart', '==', false)
        .where('startDate', '<=', new Date(Date.now() + 86400000))
        .get()
      const returnEvents = await db
        .collection('events')
        .where('hasNotifyReturn', '==', false)
        .where('returnDate', '<=', new Date(Date.now() + 86400000))
        .get()

      // Check if there are any equipment or schedule events
      // that needs to be notified
      if (
        sampleEndoscopes.size === 0 &&
        repairEndoscopes.size === 0 &&
        sampleWashers.size === 0 &&
        repairWashers.size === 0 &&
        startEvents.size === 0 &&
        returnEvents.size === 0
      ) {
        console.log('No pending notifications')
        continue
      }

      // Get all users
      const users = await db.collection('users').get()
      const usersData = users.docs.map((user) => user.data())

      console.log('Sending upcoming endoscopes sampling notifications...')
      await sendEquipmentNotification(
        usersData,
        sampleEndoscopes.docs,
        'ENDOSCOPE_UPCOMING_SAMPLE'
      )

      console.log('\nSending upcoming endoscopes repair notifications...')
      await sendEquipmentNotification(
        usersData,
        repairEndoscopes.docs,
        'ENDOSCOPE_UPCOMING_REPAIR'
      )

      console.log('\nSending upcoming washers sampling notifications...')
      await sendEquipmentNotification(
        usersData,
        sampleWashers.docs,
        'WASHER_UPCOMING_SAMPLE'
      )

      console.log('\nSending upcoming washers repair notifications...')
      await sendEquipmentNotification(
        usersData,
        repairWashers.docs,
        'WASHER_UPCOMING_REPAIR'
      )

      console.log('\nSending upcoming start event notifications...')
      await sendScheduleNotification(usersData, startEvents.docs, 'START_EVENT')

      console.log('\nSending upcoming return event notifications...')
      await sendScheduleNotification(
        usersData,
        returnEvents.docs,
        'RETURN_EVENT'
      )
    } catch (error) {
      console.error((error as any).message)
    } finally {
      console.log('Sleeping for 5 seconds...\n')
      await sleep()
    }
  }
})

async function sendScheduleNotification(
  usersData: FirebaseFirestore.DocumentData[],
  docs: FirebaseFirestore.QueryDocumentSnapshot<FirebaseFirestore.DocumentData>[],
  startOrReturn: 'START_EVENT' | 'RETURN_EVENT'
) {
  // Loop through all schedule events
  for (const doc of docs) {
    const data = doc.data()

    // Find user to notify
    const user = usersData.find((u) => u.id === data.userId)

    // Send notification if user is found
    if (user) {
      console.log(`Sending notification to ${user.name}...`)

      // Format equipment name
      const equipmentName = `${data.equipmentModel}-${data.equipmentLabel}`

      const eventType = startOrReturn === 'START_EVENT' ? '' : ' return'
      const eventName = (() => {
        switch (data.category) {
          case 'ENDOSCOPE_REPAIR':
            return 'repair event'
          case 'ENDOSCOPE_LOAN':
            return 'loan event'
          case 'ENDOSCOPE_SAMPLE':
            return 'sample event'
          case 'WASHER_SAMPLE':
            return 'sample event'
          case 'WASHER_REPAIR':
            return 'repair event'
        }
      })()
      const equipmentType = (() => {
        switch (data.category) {
          case 'ENDOSCOPE_REPAIR':
            return 'Endoscope'
          case 'ENDOSCOPE_LOAN':
            return 'Endoscope'
          case 'ENDOSCOPE_SAMPLE':
            return 'Endoscope'
          case 'WASHER_SAMPLE':
            return 'Washer'
          case 'WASHER_REPAIR':
            return 'Washer'
        }
      })()

      // Send notification to all user's devices
      const messages = user.fcmToken.map((token: string) => {
        return {
          notification: {
            title: `${equipmentType} ${equipmentName} ${eventName}`,
            body: `${equipmentType} ${equipmentName} has${eventType} ${eventName} on ${formatDateTime(
              data.returnDate.toDate()
            )}`,
          },
          token,
        }
      })

      await messaging.sendAll(messages)
      console.log(`Sent notifications to ${user.name}`)
    }

    // Create notification document
    const docId = db.collection('notifications').doc().id
    const notificationCategory = (() => {
      if (startOrReturn === 'RETURN_EVENT') {
        switch (data.category) {
          case 'ENDOSCOPE_REPAIR':
            return 'ENDOSCOPE_RETURN_REPAIR'
          case 'ENDOSCOPE_LOAN':
            return 'ENDOSCOPE_RETURN_LOAN'
          case 'ENDOSCOPE_SAMPLE':
            return 'ENDOSCOPE_RETURN_SAMPLE'
          case 'WASHER_SAMPLE':
            return 'WASHER_RETURN_SAMPLE'
          case 'WASHER_REPAIR':
            return 'WASHER_RETURN_REPAIR'
        }
      }
      return data.category
    })()

    // Save notification to database
    await db.collection('notifications').doc(docId).set({
      id: docId,
      userId: data.userId,
      category: notificationCategory,
      equipmentId: data.id,
      equipmentLabel: data.equipmentLabel,
      equipmentModel: data.equipmentModel,
      eventId: data.id,
      timestamp: new Date(),
      acknowledge: false,
    })

    // update event to notified
    const updateEvent =
      startOrReturn === 'START_EVENT'
        ? { hasNotifyStart: true }
        : { hasNotifyReturn: true }
    await db.collection('events').doc(data.id).update(updateEvent)
  }
}

async function sendEquipmentNotification(
  usersData: FirebaseFirestore.DocumentData[],
  docs: FirebaseFirestore.QueryDocumentSnapshot<FirebaseFirestore.DocumentData>[],
  category:
    | 'ENDOSCOPE_UPCOMING_SAMPLE'
    | 'ENDOSCOPE_UPCOMING_REPAIR'
    | 'WASHER_UPCOMING_SAMPLE'
    | 'WASHER_UPCOMING_REPAIR'
) {
  // Loop through all equipment documents
  for (const doc of docs) {
    const data = doc.data()

    // Find user to notify
    const user = usersData.find((u) => u.id === data.userId)

    // Send notification if user is found
    if (user) {
      console.log(`Sending notification to ${user.name}...`)
      const messages = user.fcmToken.map((token: string) => {
        // Format equipment name
        const name = `${data.model}-${data.label}`

        // Customize notification based on category
        switch (category) {
          case 'ENDOSCOPE_UPCOMING_SAMPLE':
            return {
              notification: {
                title: 'Upcoming Endoscope Sampling',
                body: `Endoscope ${name} is due for sampling on ${formatDateTime(
                  data.upcomingSample.toDate()
                )}`,
              },
              token,
            }
          case 'WASHER_UPCOMING_SAMPLE':
            return {
              notification: {
                title: 'Upcoming Washer Sampling',
                body: `Washer ${name} is due for sampling on ${formatDateTime(
                  data.upcomingSample.toDate()
                )}`,
              },
              token,
            }
          case 'ENDOSCOPE_UPCOMING_REPAIR':
            return {
              notification: {
                title: 'Upcoming Endoscope Repair',
                body: `Endoscope ${name} is due for repair on ${formatDateTime(
                  data.upcomingRepair.toDate()
                )}`,
              },
              token,
            }
          case 'WASHER_UPCOMING_REPAIR':
            return {
              notification: {
                title: 'Upcoming Washer Repair',
                body: `Endoscope ${name} is due for repair on ${formatDateTime(
                  data.upcomingRepair.toDate()
                )}`,
              },
              token,
            }
        }
      })

      // Send notification to all user's devices
      await messaging.sendAll(messages)
      console.log(`Sent notifications to ${user.name}`)
    }

    // create notification in firestore
    const docId = db.collection('notifications').doc().id
    await db.collection('notifications').doc(docId).set({
      id: docId,
      userId: data.userId,
      category,
      equipmentId: data.id,
      equipmentLabel: data.label,
      equipmentModel: data.model,
      eventId: null,
      timestamp: new Date(),
      acknowledge: false,
    })

    // update equipment that notification has been sent
    switch (category) {
      case 'ENDOSCOPE_UPCOMING_SAMPLE': {
        await db.collection('endoscopes').doc(data.id).update({
          hasNotifySample: true,
        })
      }
      case 'ENDOSCOPE_UPCOMING_REPAIR': {
        await db.collection('endoscopes').doc(data.id).update({
          hasNotifyRepair: true,
        })
      }
      case 'WASHER_UPCOMING_SAMPLE': {
        await db.collection('washers').doc(data.id).update({
          hasNotifySample: true,
        })
      }
      case 'WASHER_UPCOMING_REPAIR': {
        await db.collection('washers').doc(data.id).update({
          hasNotifyRepair: true,
        })
      }
    }
  }
}

function formatDateTime(date: Date) {
  return date.toLocaleDateString('en-US', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  })
}

function sleep(ms: number = 5000) {
  return new Promise((resolve) => {
    setTimeout(resolve, ms)
  })
}

process.on('SIGINT', async () => {
  console.log('SIGINT signal received: Exiting gracefully')
  return process.exit()
})
