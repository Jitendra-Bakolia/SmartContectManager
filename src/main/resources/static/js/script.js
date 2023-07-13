const toggleSidebar = () => {
	if ($('.sidebar').is(":visible")) {

		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");

	} else {
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%")
	}
};


const search = () => {
	console.log("Searching ......");

	let query = $("#search-input").val()


	if (query == '') {
		$(".search-result").hide();
	} else {
		//search...
		console.log(query);

		//sending request to server..

		let url = `http://localhost:8383/search/${query}`;

		fetch(url)
			.then((response) => {
				return response.json();
			})
			.then((data) => {
				//data...

				let text = `<div class='list-group'>`;

				data.forEach((contact) => {
					text += `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'>${contact.name} </a>`;
				});

				text += `</div>`;

				$(".search-result").html(text);
				$(".search-result").show();
			});


	}

};

//first request for create oreder..

const paymentStart = () => {
	let amount = $("#payment_field").val();

	if (amount == '' || amount == null) {
		swal("Error..!", "Opps Payment Failed..!", "error");
		return;
	}

	//code for create order...
	//we are using ajax to create order..
	$.ajax({
		url: '/user/create_order',
		data: JSON.stringify({ amount: amount, info: 'order_request' }),
		contentType: 'application/json',
		type: 'POST',
		dataType: 'json',

		success: function (response) {
			//this function call invoked where succes.....
			console.log(response)
			if (response.status == "created") {
				//open payment form...
				let options = {
					key: 'rzp_test_Af4jVQZjh1EYX7',
					amount: response.amount,
					currency: 'INR',
					name: 'Smart Contact Manager',
					description: 'Donation',
					image: 'data:image/png;base64,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',
					order_id: response.id,
					handler: function (response) {
						console.log(response.razorpay_payment_id)
						console.log(response.razorpay_order_id)
						console.log(response.razorpay_signature)
						console.log("payment successfull")
						//alert("payment success")

						updatePaymentOnServer(response.razorpay_payment_id, response.razorpay_order_id, 'paid');


					},
					prefill: {
						name: "",
						email: "",
						contact: ""
					},
					notes: {
						address: "DEVIL Code"
					},
					theme: {
						color: "#3399cc"
					}
				};

				let rzp = new Razorpay(options);


				rzp.on('payment.failed', function (response) {
					console.log(response.error.code);
					console.log(response.error.description);
					console.log(response.error.source);
					console.log(response.error.step);
					console.log(response.error.reason);
					console.log(response.error.metadata.order_id);
					console.log(response.error.metadata.payment_id);
					swal("Error...!", "Opps Payment Failed..!", "error");
				});


				rzp.open()
			}
		},
		error: function (error) {
			//invoked when error..
			console.log(error)
			console.log("ERROR : somthing went wrong")
		}
	})

};

function updatePaymentOnServer(payment_id, order_id, status) {
	$.ajax({
		url: '/user/update_order',
		data: JSON.stringify({ payment_id: payment_id, order_id: order_id, status: status }),
		contentType: 'application/json',
		type: 'POST',
		dataType: 'json',
		success: function (response) {
			swal("Thank-You", "Payment successfully..", "success");
		},

		error: function (error) {
			swal("Error...!", "Payment successfully from your side But somthing went wrong so we will contect you..as soon as possible", "error");
		}

	})
	
};
