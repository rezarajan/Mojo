'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendPowerNotification = functions.database.ref("Notifications").onWrite((event) => {
    const data = event.data;
    console.log('Event triggered');
    if (!data.changed()) {
        return;
    }
    const status = data.val();
    //const onOff =  status ? "on": "off";

    const payload = {
        notification: {
            //title: 'Electricity Monitor - Power status changed',
            //body: 'Test',
            //sound: "default"
			
			title: 'Electricity Monitor - Power status changed',
            body: 'Test',
            sound: "default"
			
        }
    };

    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 //24 hours
    };
    console.log('Sending notifications');
    return admin.messaging().sendToTopic("usertest", payload, options);

});