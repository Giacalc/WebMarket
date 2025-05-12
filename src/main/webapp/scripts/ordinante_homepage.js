window.onload = function() {
    const selectCategoria = document.getElementById('categoria');
    
    // se non siamo in homepage di ordinante la funzione non viene eseguita
    if (!selectCategoria) return;
    
    const scategoria = document.getElementById('scategoria');
    const mcategoria = document.getElementById('mcategoria');
    const procediButton = document.getElementById("procediButton");

    scategoria.disabled = true;
    mcategoria.disabled = true;

    // Funzione per popolare le opzioni di una select
    const populateSelect = (selectElement, options) => {
        options.forEach(option => {
        const opt = document.createElement('option');
        opt.value = option; 
        opt.textContent = option;
        selectElement.appendChild(opt);
    });
    };

    // Se cambia la selezione della categoria
    selectCategoria.addEventListener('change', function() {
        const cat_selected = this.value;

        scategoria.disabled = true;
        mcategoria.disabled = true;
        scategoria.length = 1;
        mcategoria.length = 1;

        if (cat_selected) {
            // Recupera le sottocategorie dalla servlet
            fetch(`ordinante_homepage?categoria=${cat_selected}`)
                .then(response => response.json())
                .then(data => {
                    // Popola la select delle sottocategorie
                    populateSelect(scategoria, data);
                    scategoria.disabled = false; // Abilita la select
                })
                .catch(err => console.error('Errore nel recupero delle sottocategorie:', err));
        }
        checkSelections();
    });

    // Se cambia la selezione della sottocategoria
    scategoria.addEventListener('change', function() {
        const scat_selected = this.value;

        mcategoria.disabled = true;
        mcategoria.length = 1;

        if (scat_selected) {
            // Recuperare le microcategorie dal server
            fetch(`ordinante_homepage?sottocategoria=${scat_selected}`)
                .then(response => response.json())
                .then(data => {
                    // Popola la select delle microcategorie
                    populateSelect(mcategoria, data);
                    mcategoria.disabled = false; // Abilita la select
                })
                .catch(err => console.error('Errore nel recupero delle microcategorie:', err));
        }
        checkSelections();
    });
    
    function checkSelections() {
        const categoriaValida = selectCategoria.value && selectCategoria.value !== "";
        const scategoriaValida = scategoria.value && scategoria.value !== "";
        const mcategoriaValida = mcategoria.value && mcategoria.value !== "";

        if (categoriaValida && scategoriaValida && mcategoriaValida) {
            procediButton.style.display = "inline-block";
        } else {
            procediButton.style.display = "none";
        }
    }

    selectCategoria.addEventListener("change", checkSelections);
    scategoria.addEventListener("change", checkSelections);
    mcategoria.addEventListener("change", checkSelections);
};