/* UPLOAD PROFILE PHOTO */
document.addEventListener('DOMContentLoaded',function(){
    const profilePhoto = document.querySelector("#profile-photo");
    const choosePhoto = document.querySelector("#choose-photo");
    const photoSaveButton = document.querySelector("#photo-save-button");

    const savedPhoto = localStorage.getItem('profilePhoto');
    if (savedPhoto) {
        profilePhoto.src = savedPhoto;
    }
    
    choosePhoto.addEventListener("change",()=>{
        const file = choosePhoto.files[0];
        const fileSizeInMB = file.size / (1024 * 1024);

        if (!file.type.match(/image\/(jpeg|png)/)) {
            alert("Format is not supported.");
            choosePhoto.value = ""; 
            return;
        }

        if (fileSizeInMB > 5) {
            alert("Please choose a file smaller than 5MB.");
            choosePhoto.value = ""; 
        } else {
            const reader = new FileReader();
            reader.onload = function(event) {
                localStorage.setItem('profilePhoto', event.target.result);
                profilePhoto.src = event.target.result;
            };
            reader.readAsDataURL(file);
        }
    });
    photoSaveButton.addEventListener("click", () => {
        if (choosePhoto.files.length === 0) {
            alert("Please choose a photo first!");
            return;
        }
        alert("Update profile photo successfully!");
        window.location.reload();
    });


/* CHANGE PASSWORD */
    const form = document.getElementById('password-form');
    const oldPassword = document.getElementById('oldpassword');
    const newPassword = document.getElementById('newpassword');
    const confirmPassword = document.getElementById('confirmpassword');

    form.addEventListener('submit', function(event) {
        event.preventDefault();
        if (validateInputs()) {
            alert('Change password successfully!');
            window.location.reload();
        }
    });

    const setError = (element,message) =>{
        const inputControl = element.parentElement;
        const errorDisplay = inputControl.querySelector('.error');
        errorDisplay.innerText = message;
    }
    const clearError = (element) =>{
        const inputControl = element.parentElement;
        const errorDisplay = inputControl.querySelector('.error');
        errorDisplay.innerText = '';
    }
    const clearValues = () => {
        oldPassword.value = '';
        newPassword.value = '';
        confirmPassword.value = '';
    }

    const validateInputs =() => {
        const oldPasswordValue = oldPassword.value.trim();
        const newPasswordValue = newPassword.value.trim();
        const confirmPasswordValue = confirmPassword.value.trim();
        const defaultPassword ='admin123';
        let isValid = true;

        if (oldPasswordValue === ''){
            setError(oldPassword,'Can not be empty');
            isValid = false;
            clearValues();
        } else if (oldPasswordValue !== defaultPassword) {
            setError(oldPassword, 'Wrong current password');
            isValid = false;
        }else {
            clearError(oldPassword);
        }

        if(newPasswordValue ===''){
            setError(newPassword,'Can not be empty');
            isValid = false;
            clearValues();
        } else if(newPasswordValue.length < 6){
            setError(newPassword, 'Must include at least 6 characters');
            isValid = false;
            clearValues();
        } else {
            clearError(newPassword);
        }

        if(confirmPasswordValue === ''){
            setError(confirmPassword,'Can not be empty');
            isValid = false;
            clearValues();
        } else if(confirmPasswordValue !== newPasswordValue){
            setError(confirmPassword,'Wrong password confirmation');
            isValid = false;
            clearValues();
        } else {
            clearError(confirmPassword)
        }

        return isValid;
    };
    oldPassword.addEventListener('input', function() {
        clearError(oldPassword);
    
    });
    newPassword.addEventListener('input', function() {
        clearError(newPassword);
    });
    
    confirmPassword.addEventListener('input', function() {
        clearError(confirmPassword);
    });

});



