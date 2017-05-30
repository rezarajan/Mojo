
'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendPowerNotification = functions.database.ref("Notifications/{pushId}/").onWrite((event) => {
	var db = admin.database();
	var refNode = db.ref("orders");
	var postId = refNode.push().key;
	
	refNode.push({
		alanisawesome: {
		date_of_birth: "June 23, 1912",
		full_name: "Alan Turing",
		postid: postId
		}
	});
    const data = event.data;
    console.log('Event triggered');
    if (!data.changed()) {
        return;
    }
    const status = data.val();
	
    //const onOff =  status ? "on": "off";

    const payload = {
        data: {
            //title: 'Electricity Monitor - Power status changed',
            //body: 'Test',
            //sound: "default"
			
			message: status.message,
            uid: status.uid,
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

exports.orderMonitor = functions.database.ref("orders/{pushId}/").onWrite((event) => {
	var db = admin.database();
	var refNode = db.ref("requests");
	
	//the push creates the request id
	refNode.push({
		alanisawesomeindeed: {
		customeruid: "customeruid",
		vendoruid: "vendoruid",
		result: "result",
		orderid: "orderid"
		}
	});
    const data = event.data;
    console.log('Event triggered');
    if (!data.changed()) {
        return;
    }
    const status = data.val();
	
    //const onOff =  status ? "on": "off";

    const payload = {
        data: {
            //title: 'Electricity Monitor - Power status changed',
            //body: 'Test',
            //sound: "default"
			
			message: status.message,
            uid: status.uid,
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