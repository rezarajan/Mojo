// JavaScript Document
var restaurantname = document.getElementById("restaurantname");
var itemtype = document.getElementById("itemtype");
var itemname = document.getElementById("itemname");
var itemcost = document.getElementById("itemcost");

var venue = document.getElementById("venuename");
var restaurant = document.getElementById("restaurant");
var description = document.getElementById("description");
var logo = document.getElementById("logo");
var latitude = document.getElementById("latitude");
var longitude = document.getElementById("longitude");

var submitBtn = document.getElementById("submitBtn");

function submitClick() {
	//window.alert("Submit");
	var email = "test@gmail.com";
	var password = "123456";
	firebase.auth().signInWithEmailAndPassword(email, password).catch(function(error) {
  // Handle Errors here.
  var errorCode = error.code;
  var errorMessage = error.message;
  // ...
});
	var firebaseRef = firebase.database().ref();
	
	var resName = restaurantname.value;
	var itemType = itemtype.value;
	var itemName = itemname.value;
	var itemCost = itemcost.value;
	var itemCostNumber = parseFloat(itemCost);
	firebaseRef.child("menu").child(resName).child(itemType).child("Items").child(itemName).set({
		Quantity : 100,
		cost : itemCostNumber,
		name : itemName
	});
	firebaseRef.child("menu").child(resName).child(itemType).child("type").set(itemType);
	//firebaseRef.child("Test").set(resName);


	var venuename = venuename.value;
	var restaurant = restaurant.value;
	var description = description.value;
	var logo = logo.value;
	var latitude = latitude.value;
	var longitude = longitude.value;
	var latitudeNum = parseFloat(latitude);
	var longitudeNum = parseFloat(longitude);

	firebaseRef.child("listing").child(venue).push({
		description : description,
		icon : logo,
		restaurant : restaurant
	});

	//TODO: Use geofire here to push the vendor''s location to the geofire node

	/* firebaseRef.child("geofire").child("venues").child(venuename).set({
		description : description,
		icon : logo,
		restaurant : restaurant
	}); */ 
	
}