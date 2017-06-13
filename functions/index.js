'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


/*exports.sendPowerNotification = functions.database.ref("Notifications/{pushId}/").onWrite((event) => {
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
*/

exports.orderMonitor = functions.database.ref("orders/{pushId}/").onWrite((event) => {
	const data = event.data;
    console.log('Event triggered');
    if (!data.changed()) {
        return;
    }
    const status = data.val();
	
	var db = admin.database();
	
	if(status !== null){
	var ref = db.ref("orders").child(status.postid);
	var refNode = db.ref("requests");

	ref.child("items").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
				//the push creates the request id
				refNode.child(status.postid).set({
				customeruid: status.customeruid,
				vendoruid: status.vendoruid,
				items: snapshot.val(),
				result: "asking",				//sends to the requests node with parameter asking
				orderid: status.postid
				});
				//refNode.child(status.postid).child("items").set(snapshot.val());
			} else{
				return;
			}
			
	});
	} else {
		return;
	}
});

exports.requestsMonitor = functions.database.ref("requests/{pushId}/").onWrite((event) => {
	const data = event.data;
    console.log('Event triggered');
    if (!data.changed()) {
        return;
    }
    const status = data.val();
	//var venduid = status.vendoruid;
	var database = admin.database().ref().child("uid").child(status.customeruid).child("info");
	//var userData;
	var userData;
	var userDataName;
	database.once('value')
		.then(function(dataSnapshot) {
			// handle read data.
			var userData = dataSnapshot.val();
			var userDataName = userData.name;
			console.log("User Data: " + userDataName);
			//return userDataName;
		if(status.result === "asking"){		//if asking then send to the uid node for vendor response
		var db = admin.database();
		var refNode = db.ref("uid/");
		var ref = db.ref("requests").child(status.orderid);
		
		ref.child("items").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
		refNode.child(status.vendoruid).child("requests").child(status.orderid).set({
				customeruid: status.customeruid,
				vendoruid: status.vendoruid,
				name: userDataName,
				items: snapshot.val(),
				result: "asking",
				orderid: status.orderid
		});
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}
			
	});


	} else if (status.result === "accepted"){	//if vendor accepts the order then send response to user
		var db = admin.database();
		var refNode = db.ref("inprogress/");	//changed the reference
		var ref = db.ref("requests").child(status.orderid);
		
		ref.child("items").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
		//the set uses the push key
		refNode.child(status.vendoruid).child(status.orderid).set({
				customeruid: status.customeruid,
				vendoruid: status.vendoruid,
				items: snapshot.val(),
				result: "accepted",
				orderid: status.orderid
		});
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}
			
	});

	} else if (status.result === "declined"){									//if the vendor declines the order then send response to user
		var db = admin.database();
		var refNode = db.ref("uid/");
		var ref = db.ref("requests").child(status.orderid);
		
		ref.child("items").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
		refNode.child(status.vendoruid).child("declined").child(status.orderid).set({		//vendoruid
				customeruid: status.customeruid,
				vendoruid: status.vendoruid,
				items: snapshot.val(),
				result: "declined",		//this is the only value we have to change after the first if		
				orderid: status.orderid
		});
		refNode.child(status.customeruid).child(status.orderid).set({		//customeruid
				customeruid: status.customeruid,
				vendoruid: status.vendoruid,
				items: snapshot.val(),
				result: "declined",
				orderid: status.orderid
		});
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}
			
	});

	} else if (status.result === "sending"){	//if vendor accepts the order then send response to user
		var db = admin.database();
		var refNode = db.ref("inprogress/");	//changed the reference
		var ref = db.ref("requests").child(status.orderid);
		
		ref.child("items").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
		//the set uses the push key
		refNode.child(status.vendoruid).child(status.orderid).set({
				customeruid: status.customeruid,
				vendoruid: status.vendoruid,
				items: snapshot.val(),
				result: "sending",
				orderid: status.orderid
		});
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}
			
	});

	} else if (status.result === "delivered"){	//if vendor accepts the order then send response to user
		var db = admin.database();
		var refNode = db.ref("inprogress/");	//changed the reference
		var ref = db.ref("requests").child(status.orderid);
		
		ref.child("items").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
		//the set uses the push key
		refNode.child(status.vendoruid).child(status.orderid).set({
				customeruid: status.customeruid,
				vendoruid: status.vendoruid,
				items: snapshot.val(),
				result: "delivered",
				orderid: status.orderid
		});
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}
			
	});

	}
	});

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
	var ref = db.ref("requests").child(status.orderid);
	
	if (status.result === "accepted"){	//if vendor accepts the order then send response to user
				ref.child("items").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
	refNode.child(status.vendoruid).child("accepted").child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			items: snapshot.val(),
			result: "accepted",
			orderid: status.orderid
	});
	refNode.child(status.customeruid).child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			items: snapshot.val(),
			result: "accepted",
			orderid: status.orderid
	});
	/*refNode.child(status.runneruid).child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			runneruid: status.runneruid,
			items: snapshot.val(),
			result: "accepted",
			orderid: status.orderid
	}); */
				//TODO: Use this to insert the runnerUID and then make a runnerUID monitor
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}
			
	});
		
	} else if (status.result === "sending"){	//if vendor accepts the order then send response to user
				ref.child("items").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
	refNode.child(status.vendoruid).child("sending").child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			items: snapshot.val(),
			result: "sending",
			orderid: status.orderid
	});
	refNode.child(status.customeruid).child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			items: snapshot.val(),
			result: "sending",
			orderid: status.orderid
	});
	/*refNode.child(status.runneruid).child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			runneruid: status.runneruid,
			items: snapshot.val(),
			result: "sending",
			orderid: status.orderid
	}); */
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}
			
	});
		
	} else if (status.result === "delivered"){	//if vendor accepts the order then send response to user
				ref.child("items").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
	refNode.child(status.vendoruid).child("delivered").child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			items: snapshot.val(),
			result: "delivered",
			orderid: status.orderid
	});
	refNode.child(status.customeruid).child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			items: snapshot.val(),
			result: "delivered",
			orderid: status.orderid
	});
	/*refNode.child(status.runneruid).child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			runneruid: status.runneruid,
			items: snapshot.val(),
			result: "sending",
			orderid: status.orderid
	}); */
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}
			
	});
		
	}
	


});

exports.requestOrderMonitor = functions.database.ref("uid/{uid}/requests/{pushId}").onWrite((event) => {		//notifies user/vendor
	const data = event.data;
    console.log('Event triggered');
    if (!data.changed()) {
        return;
    }
    const status = data.val();
	
	if(status != null){
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
    console.log('Sending requests notifications');
    //return admin.messaging().sendToTopic("usertest", payload, options);
	
	console.log("Vendor: " + status.vendoruid);
	return admin.messaging().sendToTopic(status.vendoruid, payload, options);		//using the vendoruid as the topic
	}
	else{
		return;
	}
	
	

});

exports.acceptedOrderMonitor = functions.database.ref("uid/{uid}/accepted/{pushId}").onWrite((event) => {		//notifies user/vendor
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
    console.log('Sending accepted notifications');
    //return admin.messaging().sendToTopic("usertest", payload, options);
	
	var database = admin.database().ref().child("orders").child(status.orderid);
	//var userData;
	var userToken;
	var userTokenid;
	
	database.once('value')
		.then(function(dataSnapshot) {
			// handle read data.
			var userToken = dataSnapshot.val();
			var userTokenid = userToken.user_token;
			console.log("User Token: " + userTokenid);
			return admin.messaging().sendToDevice(userTokenid, payload, options);		//using the user_token as the receiver

		});
});

exports.sendingOrderMonitor = functions.database.ref("uid/{uid}/sending/{pushId}").onWrite((event) => {		//notifies user/vendor
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
		//runneruid: status.runneruid,
		message: status.result,
        sound: "default"
		
    }
		
    };

    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 //24 hours
    };
    console.log('Sending delivery notifications');
    //return admin.messaging().sendToTopic("usertest", payload, options);
	
	var db = admin.database();
	var ref = db.ref().child("uid").child(status.vendoruid);
	ref.orderByKey().equalTo(status.orderid).on("child_added", function(snapshot) {
	console.log(snapshot.key);
	//ref.child("requests").child(snapshot.key).remove();		//removes the order from requests after the restaurant has accepted it
	ref.child("accepted").child(snapshot.key).remove();		//removes the order from accepted after the restaurant has accepted it

});
	
	var database = admin.database().ref().child("orders").child(status.orderid);
	//var userData;
	var userToken;
	var userTokenid;
	
	database.once('value')
		.then(function(dataSnapshot) {
			// handle read data.
			var userToken = dataSnapshot.val();
			var userTokenid = userToken.user_token;
			console.log("User Token: " + userTokenid);
			return admin.messaging().sendToDevice(userTokenid, payload, options);		//using the user_token as the receiver
			//return admin.messaging().sendToDevice(runneruserTokenid, payload, options);		//using the user_token as the receiver for the runner

		});
});

exports.declinedOrderMonitor = functions.database.ref("uid/{uid}/declined/{pushId}").onWrite((event) => {		//notifies user/vendor
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
    console.log('Sending delivery notifications');
    //return admin.messaging().sendToTopic("usertest", payload, options);
	
	var db = admin.database();
	var ref = db.ref().child("uid").child(status.vendoruid).child("requests");
	ref.orderByKey().equalTo(status.orderid).on("child_added", function(snapshot) {
	console.log(snapshot.key);
	ref.child(snapshot.key).remove();		//removes the order from requests after the restaurant has accepted it
});
	
	var database = admin.database().ref().child("orders").child(status.orderid);
	//var userData;
	var userToken;
	var userTokenid;
	
	database.once('value')
		.then(function(dataSnapshot) {
			// handle read data.
			var userToken = dataSnapshot.val();
			var userTokenid = userToken.user_token;
			console.log("User Token: " + userTokenid);
			return admin.messaging().sendToDevice(userTokenid, payload, options);		//using the user_token as the receiver

		});
});

exports.deliveredOrderMonitor = functions.database.ref("uid/{uid}/delivered/{pushId}").onWrite((event) => {		//notifies user/vendor
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
    console.log('Sending delivery notifications');
    //return admin.messaging().sendToTopic("usertest", payload, options);
	
	var db = admin.database();
	var ref = db.ref().child("uid").child(status.vendoruid).child("requests");
	var ref2 = db.ref().child("uid").child(status.vendoruid).child("sending");
	ref.orderByKey().equalTo(status.orderid).on("child_added", function(snapshot) {
	console.log(snapshot.key);
	ref.child(snapshot.key).remove();		//removes the order from sending after the restaurant has delivered it
		console.log('Request removed');
	ref2.child(snapshot.key).remove();		//removes the order from sending after the restaurant has delivered it
		console.log('Sending removed');
});
	
	var database = admin.database().ref().child("orders").child(status.orderid);
	//var userData;
	var userToken;
	var userTokenid;
	
	database.once('value')
		.then(function(dataSnapshot) {
			// handle read data.
			var userToken = dataSnapshot.val();
			var userTokenid = userToken.user_token;
			console.log("User Token: " + userTokenid);
			return admin.messaging().sendToDevice(userTokenid, payload, options);		//using the user_token as the receiver
			//return admin.messaging().sendToDevice(vendoruserTokenid, payload, options);		//using the user_token as the receiver for the vendor

		});
});