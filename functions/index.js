'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

const stripe = require('stripe')(functions.config().stripe.token),
      currency = functions.config().stripe.currency || 'USD';

      // [START chargecustomer]
      // Charge the Stripe customer whenever an amount is written to the Realtime database
      exports.createStripeCharge = functions.database.ref('/stripe_customers/{userId}/charges/{pushId}').onWrite(event => {
        const val = event.data.val();
        // This onWrite will trigger whenever anything is written to the path, so
        // noop if the charge was deleted, errored out, or the Stripe API returned a result (id exists)
        if (val === null || val.id || val.error) return null;
        // Look up the Stripe customer id written in createStripeCustomer
        return admin.database().ref(`/stripe_customers/${event.params.userId}/customer_id`).once('value').then(snapshot => {
          return snapshot.val();
        }).then(customer => {
          // Create a charge using the pushId as the idempotency key, protecting against double charges
          const amount = val.amount;
          const idempotency_key = event.params.id;
          let charge = {amount, currency, customer};
          if (val.source !== null) charge.source = val.source;
          return stripe.charges.create(charge, {idempotency_key});
        }).then(response => {
            // If the result is successful, write it back to the database
            return event.data.adminRef.set(response);
          }, error => {
            console.log('Error');
          }
        );
      });
      // [END chargecustomer]]

      // When a user is created, register them with Stripe
      exports.createStripeCustomer = functions.auth.user().onCreate(event => {
        const data = event.data;
        return stripe.customers.create({
          email: data.email
        }).then(customer => {
          return admin.database().ref(`/stripe_customers/${data.uid}/customer_id`).set(customer.id);
        });
      });

      // Add a payment source (card) for a user by writing a stripe payment source token to Realtime database
      exports.addPaymentSource = functions.database.ref('/stripe_customers/{userId}/sources/{pushId}/token').onWrite(event => {
        const source = event.data.val();
        if (source === null) return null;
        return admin.database().ref(`/stripe_customers/${event.params.userId}/customer_id`).once('value').then(snapshot => {
          return snapshot.val();
        }).then(customer => {
          return stripe.customers.createSource(customer, {source});
        }).then(response => {
            return event.data.adminRef.parent.set(response);
          }, error => {
            console.log('Error');
        });
      });

      // When a user deletes their account, clean up after them
      exports.cleanupUser = functions.auth.user().onDelete(event => {
        return admin.database().ref(`/stripe_customers/${event.data.uid}`).once('value').then(snapshot => {
          return snapshot.val();
        }).then(customer => {
          return stripe.customers.del(customer);
        }).then(() => {
          return admin.database().ref(`/stripe_customers/${event.data.uid}`).remove();
        });
      });


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
				customeruid_to: status.customeruid_to,
        customeruid_from: status.customeruid_from,
				vendoruid: status.vendoruid,
				items: snapshot.val(),
				//result: "asking",				//sends to the requests node with parameter asking
        result: "transient",				//sends to the requests node with parameter transient for gifted food from others
				orderid: status.postid
				});
				//refNode.child(status.postid).child("items").set(snapshot.val());
			} else{
				return;
			}

	});
	ref.child("cost").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
				//the push creates the request id
				refNode.child(status.postid).update({
				cost: snapshot.val()
				});
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
	var database = admin.database().ref().child("uid").child(status.customeruid_to).child("info");
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
      if(status.result === "transient"){		//if transient then send to the uid node for target user response
      var db = admin.database();
      var refNode = db.ref("uid/");
      var ref = db.ref("requests").child(status.orderid);

      ref.child("items").once("value")
      .then(function(snapshot) {
        if(snapshot.val() !== null){
      //the set uses the push key
      refNode.child(status.customeruid_to).child("gifts").child(status.orderid).set({
          customeruid_to: status.customeruid_to,
          customeruid_from: status.customeruid_from,
          vendoruid: status.vendoruid,
          //runneruid: status.runneruid,
          name: userDataName,
          items: snapshot.val(),
          result: "transient",
          orderid: status.orderid
      });
          //refNode.child(status.orderid).child("items").set(snapshot.val());
        } else{
          return;
        }
        });

      ref.child("cost").once("value")
      .then(function(snapshot) {
        if(snapshot.val() !== null){
      //the set uses the push key
      refNode.child(status.customeruid_to).child("gifts").child(status.orderid).update({
          cost: snapshot.val()
      });
          //refNode.child(status.orderid).child("items").set(snapshot.val());
        } else{
          return;
        }

    });


  } else if(status.result === "asking"){		//if asking then send to the uid node for vendor response
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
				//runneruid: status.runneruid,
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

		ref.child("cost").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
		refNode.child(status.vendoruid).child("requests").child(status.orderid).update({
				cost: snapshot.val()
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
		ref.child("cost").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
		refNode.child(status.vendoruid).child("requests").child(status.orderid).update({
				cost: snapshot.val()
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

		ref.child("cost").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
		refNode.child(status.vendoruid).child("declined").child(status.orderid).update({		//vendoruid
				cost: snapshot.val()
		});
		refNode.child(status.customeruid).child(status.orderid).update({		//customeruid
				cost: snapshot.val()
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
				//runneruid: status.runneruid,
				runneruid: "Runner",		//this is just for testing. TODO: create a method to correctly find a runner
				items: snapshot.val(),
				result: "sending",
				orderid: status.orderid
		});
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}
		});

		ref.child("cost").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
		//the set uses the push key
		refNode.child(status.vendoruid).child(status.orderid).update({
				items: snapshot.val()
		});
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}

	});

	} else if (status.result === "collected"){	//if runner collects the order then send response to user and vendor
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
				//runneruid: status.runneruid,		//since the runneruid has already been set to "Runner" in sending case
				runneruid: "Runner",
				items: snapshot.val(),
				result: "collected",
				orderid: status.orderid
		});
		ref.update({
				runneruid: "Runner"
		});
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}
		});
		ref.child("cost").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
		//the set uses the push key
		refNode.child(status.vendoruid).child(status.orderid).update({
				cost: snapshot.val()
		});
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
				runneruid: status.runneruid,		//since the runneruid has already been set to "Runner" in sending case
				items: snapshot.val(),		//gets the order items
				result: "delivered",
				orderid: status.orderid
		});
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}

	});
	ref.child("cost").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
		//the set uses the push key
		refNode.child(status.vendoruid).child(status.orderid).update({
				cost: snapshot.val()
		});
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
				//////DO NOT NEED
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
	ref.child("cost").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
	refNode.child(status.vendoruid).child("accepted").child(status.orderid).update({
			cost: snapshot.val()
	});
	refNode.child(status.customeruid).child(status.orderid).update({
			cost: snapshot.val()
	});
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
	refNode.child(status.runneruid).child("sending").child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			runneruid: status.runneruid,
			items: snapshot.val(),
			result: "sending",
			orderid: status.orderid
	});
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}

	});
	ref.child("cost").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
	refNode.child(status.vendoruid).child("sending").child(status.orderid).update({
			cost: snapshot.val()
	});
	refNode.child(status.customeruid).child(status.orderid).update({
			cost: snapshot.val()
	});
	refNode.child(status.runneruid).child("sending").child(status.orderid).update({
			cost: snapshot.val()
	});
			} else{
				return;
			}

	});

	} else if (status.result === "collected"){	//if runner collects the order then send response to user and vendor
	ref.child("items").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
	refNode.child(status.vendoruid).child("collected").child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			items: snapshot.val(),
			result: "collected",
			orderid: status.orderid
	});
	refNode.child(status.customeruid).child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			items: snapshot.val(),
			result: "collected",
			orderid: status.orderid
	});
	refNode.child(status.runneruid).child("collected").child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			runneruid: status.runneruid,
			items: snapshot.val(),
			result: "collected",
			orderid: status.orderid
	});
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}

	});

	ref.child("cost").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
	refNode.child(status.vendoruid).child("collected").child(status.orderid).update({
			cost: snapshot.val()
	});
	refNode.child(status.customeruid).child(status.orderid).update({
			cost: snapshot.val()
	});
	refNode.child(status.runneruid).child("collected").child(status.orderid).update({
			cost: snapshot.val()
	});
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
			runneruid: status.runneruid,
			items: snapshot.val(),
			result: "delivered",
			orderid: status.orderid
	});
	refNode.child(status.customeruid).child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			runneruid: status.runneruid,
			items: snapshot.val(),
			result: "delivered",
			orderid: status.orderid
	});
	refNode.child(status.runneruid).child("delivered").child(status.orderid).set({
			customeruid: status.customeruid,
			vendoruid: status.vendoruid,
			runneruid: status.runneruid,
			items: snapshot.val(),
			result: "delivered",
			orderid: status.orderid
	});
				//refNode.child(status.orderid).child("items").set(snapshot.val());
			} else{
				return;
			}

	});
	ref.child("cost").once("value")
		.then(function(snapshot) {
			if(snapshot.val() !== null){
		//the set uses the push key
	refNode.child(status.vendoruid).child("delivered").child(status.orderid).update({
			cost: snapshot.val()
	});
	refNode.child(status.customeruid).child(status.orderid).update({
			cost: snapshot.val()
	});
	refNode.child(status.runneruid).child("delivered").child(status.orderid).update({
			cost: snapshot.val()
	});
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

	if(status != null){		//to account for the deletion case
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

exports.giftsOrderMonitor = functions.database.ref("uid/{uid}/gifts/{pushId}").onWrite((event) => {		//notifies user/vendor
	const data = event.data;
    console.log('Event triggered');
    if (!data.changed()) {
        return;
    }
    const status = data.val();

if(status.result === "asking"){		//if asking then send to the uid node for vendor response
  		var db = admin.database();
  		var refNode = db.ref("uid/");
  		var ref = db.ref("requests").child(status.orderid);

  		//the set uses the push key
  		ref.set({
  				customeruid_to: status.customeruid_to,
          customeruid_from: status.customeruid_from,
  				vendoruid: status.vendoruid,
  				//runneruid: status.runneruid,
  				//items: snapshot.val(),
  				result: "asking",
  				orderid: status.orderid
  		});
  				//refNode.child(status.orderid).child("items").set(snapshot.val());


  } else if(status.result === "declined"){		//if asking then send to the uid node for vendor response
    		var db = admin.database();
    		var refNode = db.ref("uid/");
    		var ref = db.ref("requests").child(status.orderid);

    		//the set uses the push key
    		ref.set({
          customeruid_to: status.customeruid_to,
          customeruid_from: status.customeruid_from,
    				vendoruid: status.vendoruid,
    				//runneruid: status.runneruid,
    				//items: snapshot.val(),
    				result: "declined",
    				orderid: status.orderid
    		});
    				//refNode.child(status.orderid).child("items").set(snapshot.val());

    			}



	if(status != null){		//to account for the deletion case
		const payload = {
    data: {
        //title: 'Electricity Monitor - Power status changed',
        //body: 'Test',
        //sound: "default"

		//customeruid_to: status.customeruid_to,
    //customeruid_from: status.customeruid_from,
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

	console.log("Recipient: " + status.customeruid_to);
	return admin.messaging().sendToTopic(status.customeruid_to, payload, options);		//using the vendoruid as the topic
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
	} else {
		return;
	}
});

exports.sendingOrderMonitor = functions.database.ref("uid/{uid}/sending/{pushId}").onWrite((event) => {		//notifies user/vendor
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
			var userTokenid = userToken.user_token;		//gets the customer's user token
			console.log("User Token: " + userTokenid);
			return admin.messaging().sendToDevice(userTokenid, payload, options);		//using the user_token as the receiver
			//return admin.messaging().sendToDevice(runneruserTokenid, payload, options);		//using the user_token as the receiver for the runner
			return admin.messaging().sendToTopic("Runner", payload, options);		//this is a test topic for the runner until the actual runner user token is set up. Random sampling should be done to acquire this and then the information is set in the sending node for runnerUID.

		});
	} else{
		return;
	}
});

exports.collectedOrderMonitor = functions.database.ref("uid/{uid}/collected/{pushId}").onWrite((event) => {		//notifies user/vendor
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
	ref.orderByKey().equalTo(status.orderid).on("child_added", function(snapshot) {		//for the vendor
	console.log(snapshot.key);
	//ref.child("requests").child(snapshot.key).remove();		//removes the order from requests after the restaurant has accepted it
	ref.child("sending").child(snapshot.key).remove();		//removes the order from sending after the restaurant has accepted it
		console.log('Vendor: Sending removed');

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
			return admin.messaging().sendToTopic("Starbucks", payload, options);		//sends the notificaiton that the runner has collected the food to the restaurant. In this case, the test restaurant is Starbucks.

		});
	} else {
		return;
	}
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
	var ref2 = db.ref().child("uid").child(status.vendoruid).child("accepted");
	var ref3 = db.ref().child("uid").child(status.vendoruid).child("sending");
	var ref4 = db.ref().child("uid").child(status.vendoruid).child("collected");
	var ref5 = db.ref().child("uid").child(status.runneruid).child("sending");
	var ref6 = db.ref().child("uid").child(status.runneruid).child("collected");
	ref.orderByKey().equalTo(status.orderid).on("child_added", function(snapshot) {
		//set up for the case of the vendoruid write, which then deletes the node and then the runneruid write which would then be null since the node has just been deleted
		if(snapshot.key !== null){
	console.log(snapshot.key);
	ref.child(snapshot.key).remove();		//removes the order from sending after the restaurant has delivered it
		console.log('Vendor: Request removed');
	ref2.child(snapshot.key).remove();		//removes the order from sending after the restaurant has delivered it
		console.log('Vendor: Accepted removed');
	ref3.child(snapshot.key).remove();		//removes the order from sending after the restaurant has delivered it
		console.log('Vendor: Sending removed');
	ref4.child(snapshot.key).remove();		//removes the order from sending after the restaurant has delivered it
		console.log('Vendor: Collected removed');
	ref5.child(snapshot.key).remove();		//removes the order from sending after the restaurant has delivered it
		console.log('Runner: Sending removed');
	ref6.child(snapshot.key).remove();		//removes the order from sending after the restaurant has delivered it
		console.log('Runner: Collected removed');
		} else{
			return;
		}
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
			return admin.messaging().sendToTopic("Starbucks", payload, options);		//sends the notificaiton that the runner has delivered the food to the restaurant. In this case, the test restaurant is Starbucks.

		});
});
