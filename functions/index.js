
'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendPowerNotification = functions.database.ref("Notifications/{pushId}/").onWrite((event) => {
	var db = admin.database();
	var refNode = db.ref("orders");
	refNode.push();
	var postId = refNode.push().key;
	
	refNode.child(postId).set({
		customeruid: "customeruid",
		vendoruid: "vendoruid",
		items: "items",
		postid: postId
		
		//alanisawesome: {
		//date_of_birth: "June 23, 1912",
		//full_name: "Alan Turing"
		//}
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
	
    const data = event.data;
    console.log('Event triggered');
    if (!data.changed()) {
        return;
    }
    const status = data.val();
	
	//the push creates the request id
	refNode.push({
		customeruid: "customeruid",
		vendoruid: "vendoruid",
		result: "result",
		orderid: status.postid
	});

});

exports.requestsMonitor = functions.database.ref("requests/{pushId}/").onWrite((event) => {
	const data = event.data;
    console.log('Event triggered');
    if (!data.changed()) {
        return;
    }
    const status = data.val();
	//var venduid = status.vendoruid;
	
	var db = admin.database();
	var refNode = db.ref("uid/");
	
	//the push creates the request id
	refNode.child(status.vendoruid).set({
		customeruid: "customeruid",
		vendoruid: "vendoruid",
		result: "result",
		orderid: status.orderid
	});

});

exports.uidMonitor = functions.database.ref("uid/{uid}/").onWrite((event) => {
	const data = event.data;
    console.log('Event triggered');
    if (!data.changed()) {
        return;
    }
    const status = data.val();
	const payload = {
    data: {
        //title: 'Electricity Monitor - Power status changed',
        //body: 'Test',
        //sound: "default"
		
		customeruid: status.customeruid,
        vendoruid: status.vendoruid,
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