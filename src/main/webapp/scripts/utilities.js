document.addEventListener("DOMContentLoaded", () => {
  const footer = document.getElementById("footer");
  const checkFooterPosition = () => {
    // Controllo su scrolling attivo
    const isScrollActive = document.documentElement.clientHeight < document.documentElement.scrollHeight;

    if (isScrollActive) {
      footer.classList.remove("w3-bottom"); 
    } else {
      footer.classList.add("w3-bottom");
    }
  };

  checkFooterPosition();

  // Aggiornamento dinamico
  window.addEventListener("resize", checkFooterPosition);
});