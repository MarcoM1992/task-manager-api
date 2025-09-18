document.addEventListener("DOMContentLoaded", function() {
	console.log("prova");
    const form = document.getElementById("resetForm");
    const errorPassword = document.getElementById("errorPassword");
    const errorConfirm = document.getElementById("errorConfirm");

    form.addEventListener("submit", function(event) {
        resetErrors();

        const password = form.newPassword.value;
        const confirm = form.confirmPassword.value;

        let valid = true;

        if(password.length === 0){
            errorPassword.innerText = "Password cannot be empty";
            errorPassword.style.visibility = "visible";
            valid = false;
        } else if(password.length < 8){
            errorPassword.innerText = "Password must be at least 8 characters";
            errorPassword.style.visibility = "visible";
            valid = false;
        }

        if(confirm.length === 0){
            errorConfirm.innerText = "Confirm password cannot be empty";
            errorConfirm.style.visibility = "visible";
            valid = false;
        } else if(password !== confirm) {
            errorConfirm.innerText = "Passwords do not match";
            errorConfirm.style.visibility = "visible";
            valid = false;
        }

        if(!valid){
            event.preventDefault(); // blocca il submit
        }
    });

    function resetErrors(){
        errorPassword.style.visibility = "hidden";
        errorConfirm.style.visibility = "hidden";
        errorPassword.innerText = "";
        errorConfirm.innerText = "";
    }
});
