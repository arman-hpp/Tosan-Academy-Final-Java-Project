// Example starter JavaScript for disabling form submissions if there are invalid fields
(function () {
  'use strict'
  var psw = document.getElementById("registerPassword");
  psw.addEventListener("keyup", validatePassword);
  
  var rePsw = document.getElementById("registerRepeatPassword"); 
  rePsw.addEventListener("keyup", validatePassword);

  var forms = document.querySelectorAll('.needs-validation');
  Array.prototype.slice.call(forms)
    .forEach(function (form) {
      form.addEventListener('submit', function (event) {OnSubmit(form, event)}, false);
    })
})();

function OnSubmit(sender, eventArgs) {
	if (!sender.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
    }

    sender.classList.add('was-validated');
}

function validatePassword() {
    var psw = document.getElementById("registerPassword");
    var rePsw = document.getElementById("registerRepeatPassword");


    if (!psw.validity.valid) {
      psw.setCustomValidity('Lütfen işaretli yerleri doldurunuz');  
    }
	
    if (psw.value !== rePsw.value) {
        rePsw.setCustomValidity("Those passwords didn't match");
    }
    else {
        rePsw.setCustomValidity("");
    }
}
