function checkEmail() {
    var email = document.getElementById("teamMemberEmail").value.trim();
    var projectId = document.getElementById("projectId").value; // hidden input의 ID가 "projectId"인지 확인

    var emailStatus = document.getElementById("emailStatus");

    if (email === '') {
        emailStatus.textContent = "Please enter an email.";
        emailStatus.style.color = "red";
        return;
    }

    // 비동기 요청
    fetch(`/checkTeamMemberEmail?email=${encodeURIComponent(email)}&projectId=${encodeURIComponent(projectId)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.exists) {
                emailStatus.textContent = "This email is already a team member.";
                emailStatus.style.color = "red";
            } else if (data.valid) {
                emailStatus.textContent = "This email is valid and can be added.";
                emailStatus.style.color = "green";
            } else {
                emailStatus.textContent = "This email does not exist.";
                emailStatus.style.color = "red";
            }
        })
        .catch(error => {
            emailStatus.textContent = "Error checking email.";
            emailStatus.style.color = "red";
            console.error('Error:', error);
        });
}
