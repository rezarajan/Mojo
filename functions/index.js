
'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendPowerNotification = functions.database.ref("Notifications/{pushId}/").onWrite((event) => {
    const data = event.data;
    console.log('Event triggered');
    if (!data.changed()) {
        return;
    }
    const status = data.val();
	
    //const onOff =  status ? "on": "off";
	
	var db = admin.database();
	var refNode = db.ref("orders");
	refNode.push();
	var postId = refNode.push().key;
	
	refNode.child(postId).set({
		customeruid: status.customeruid,
		vendoruid: status.vendoruid,
		items: status.items,
		postid: postId
		
		//alanisawesome: {
		//date_of_birth: "June 23, 1912",
		//full_name: "Alan Turing"
		//}
	});

});

exports.orderMonitor = functions.database.ref("orders/{pushId}/").onWrite((event) => {
	const data = event.data;
    console.log('Event triggered');
    if (!data.changed()) {
        return;
    }
    const status = data.val();
	
	var db = admin.database();
	var refNode = db.ref("requests");
	
	//the push creates the request id
	refNode.child(status.postid).set({
		customeruid: status.customeruid,
		vendoruid: status.vendoruid,
		items: status.items,
		result: "asking",				//sends to the requests node with parameter asking
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
	
	if(status.result == "asking"){		//if asking then send to the uid node for vendor response
		var db = admin.database();
		var refNode = db.ref("uid/");
		//the set uses the push key
		refNode.child(status.vendoruid).child(status.orderid).set({
				customeruid: status.customeruid,
				vendoruid: status.vendoruid,
				items: status.items,
				result: "asking",
				orderid: status.orderid
		});
	} else if (status.result == "accepted"){	//if vendor accepts the order then send response to user
		var db = admin.database();
		var refNode = db.ref("inprogress/");	//changed the reference
		//the set uses the push key
		refNode.child(status.vendoruid).child(status.orderid).set({
				customeruid: status.customeruid,
				vendoruid: status.vendoruid,
				items: status.items,
				result: "accepted",
				orderid: status.orderid
		});
	} else {									//if the vendor declines the order then send response to user
		var db = admin.database();
		var refNode = db.ref("uid/");
		//the set uses the push key
		refNode.child(status.vendoruid).child(status.orderid).set({
				customeruid: status.customeruid,
				vendoruid: status.vendoruid,
				items: status.items,
				result: "declined",		//this is the only value we have to change after the first if		
				orderid: status.orderid
		});
		refNode.child(status.customeruid).child(status.orderid).set({
				customeruid: status.customeruid,
				vendoruid: status.vendoruid,
				items: status.items,
				result: "declined",
				orderid: status.orderid
		});
	}

});

exports.inprogressMonitor = functions.database.ref("inprogress/{vendoruid}/{pushId}/").onWrite((event) => {
	const data = event.data;
    console.log('Event triggered');
    if (!data.changed()) {
        return;
    }
    const status = data.val();
	
	var db = admin.database();
	var refNode = db.ref("uid/");
	
	refNode.child(status.customeruid).child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			items: status.items,
			result: "accepted",
			orderid: status.orderid
	});

});

exports.uidMonitor = functions.database.ref("uid/{uid}/{pushId}").onWrite((event) => {		//notifies user/vendor
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
		message: status.result,
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