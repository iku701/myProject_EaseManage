// JavaScript로 외부 HTML 파일 로드
document.addEventListener("DOMContentLoaded", function () {
    const navbarContainer = document.getElementById("navbar-container");

    fetch("navbar.html")
        .then(response => response.text())
        .then(data => {
            navbarContainer.innerHTML = data;
        })
        .catch(error => console.error("Error loading navbar:", error));
});

// Example of showing a notification
function showCustomNotification(message, type) {
    $.notify({
        icon: "nc-icon nc-bell-55",
        message: message
    }, {
        type: type,
        timer: 3000,
        placement: {
            from: "top",
            align: "right"
        }
    });
}