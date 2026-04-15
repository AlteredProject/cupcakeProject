document.querySelectorAll("details").forEach(d => {
    d.addEventListener("toggle", function() {
        if (this.open) {
            document.querySelectorAll("details").forEach(other => {
                if (other !== this) {
                    other.removeAttribute("open");
                }
            });
        }
    });
});