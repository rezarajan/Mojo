// JavaScript Document
var restaurantname = document.getElementById("restaurantname");
var itemtype = document.getElementById("itemtype");
var itemname = document.getElementById("itemname");
var itemcost = document.getElementById("itemcost");

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
	
}