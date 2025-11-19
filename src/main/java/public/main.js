// --- VARIABILI GLOBALI ---
let utenteCorrente = null;
let idModificaLibro = null;
let idModificaUtente = null;
let linguaCorrente = 'it'; // Default Italiano

// --- DIZIONARIO TRADUZIONI ---
const traduzioni = {
    it: {
        // Navbar
        nav_libri: "📖 Catalogo Libri",
        nav_utenti: "👥 Gestione Utenti",
        nav_prestiti: "📅 Registro Prestiti",
        btn_logout: "Esci",
        welcome_guest: "Benvenuto",
        
        // Login
        login_title: "🔐 Accesso Libreria",
        login_who: "Chi sei?",
        role_admin: "Bibliotecario (Admin)",
        role_client: "Cliente",
        lbl_nome: "Nome",
        lbl_pass: "Password",
        btn_login: "Accedi",
        login_footer: "Non hai dati? Usa il generatore",
        btn_popola: "Popola DB",

        // Sezione Libri
        title_libri: "📖 Catalogo Libri",
        card_libri: "Aggiungi / Modifica Libro",
        ph_titolo: "Titolo",
        ph_autore: "Autore",
        ph_genere: "Genere",
        btn_salva: "Salva",
        th_titolo: "Titolo",
        th_autore: "Autore",
        th_genere: "Genere",
        th_azioni: "Azioni",

        // Sezione Utenti
        title_utenti: "👥 Gestione Utenti",
        card_utenti: "Nuovo / Modifica Utente",
        ph_nome: "Nome",
        ph_cognome: "Cognome",
        ph_eta: "Età",
        btn_crea: "Crea",
        th_nome: "Nome",
        th_ruolo: "Ruolo",

        // Sezione Prestiti
        title_prestiti: "📅 Registro Prestiti",
        card_prestiti: "Registra Prestito",
        ph_id_ute: "ID Utente",
        ph_id_lib: "ID Libro",
        btn_registra: "Registra",
        th_utente: "Utente",
        th_libro: "Libro",
        th_data: "Data Prestito",
        th_rest: "Restituzione",

        // Messaggi JavaScript (Alert)
        msg_login_ok: "Login effettuato!",
        msg_login_err: "Credenziali errate o utente non trovato",
        msg_campi: "Compila tutti i campi!",
        msg_fatto: "Operazione completata!",
        msg_conferma: "Sei sicuro?",
        msg_creato: "Dati Generati con successo!"
    },
    en: {
        // Navbar
        nav_libri: "📖 Book Catalog",
        nav_utenti: "👥 User Management",
        nav_prestiti: "📅 Loan Registry",
        btn_logout: "Logout",
        welcome_guest: "Welcome",

        // Login
        login_title: "🔐 Library Access",
        login_who: "Who are you?",
        role_admin: "Librarian (Admin)",
        role_client: "Customer",
        lbl_nome: "Name",
        lbl_pass: "Password",
        btn_login: "Sign In",
        login_footer: "No data? Use generator",
        btn_popola: "Populate DB",

        // Books
        title_libri: "📖 Book Catalog",
        card_libri: "Add / Edit Book",
        ph_titolo: "Title",
        ph_autore: "Author",
        ph_genere: "Genre",
        btn_salva: "Save",
        th_titolo: "Title",
        th_autore: "Author",
        th_genere: "Genre",
        th_azioni: "Actions",

        // Users
        title_utenti: "👥 User Management",
        card_utenti: "New / Edit User",
        ph_nome: "Name",
        ph_cognome: "Surname",
        ph_eta: "Age",
        btn_crea: "Create",
        th_nome: "Name",
        th_ruolo: "Role",

        // Loans
        title_prestiti: "📅 Loan Registry",
        card_prestiti: "Register Loan",
        ph_id_ute: "User ID",
        ph_id_lib: "Book ID",
        btn_registra: "Register",
        th_utente: "User",
        th_libro: "Book",
        th_data: "Loan Date",
        th_rest: "Return",

        // JS Alerts
        msg_login_ok: "Login successful!",
        msg_login_err: "Wrong credentials or user not found",
        msg_campi: "Please fill all fields!",
        msg_fatto: "Operation completed!",
        msg_conferma: "Are you sure?",
        msg_creato: "Data generated successfully!"
    }
};

// --- FUNZIONE CAMBIO LINGUA ---
function cambiaLingua(lang) {
    linguaCorrente = lang;
    const t = traduzioni[lang];

    // 1. Traduce tutti gli elementi con attributo data-i18n
    document.querySelectorAll('[data-i18n]').forEach(el => {
        const key = el.getAttribute('data-i18n');
        if (t[key]) {
            // Se è un input con placeholder
            if (el.tagName === 'INPUT' && el.hasAttribute('placeholder')) {
                el.placeholder = t[key];
            } else {
                el.innerText = t[key];
            }
        }
    });

    // 2. Aggiorna messaggio di benvenuto se loggato
    if (utenteCorrente) {
        const msg = lang === 'it' ? `Ciao, ${utenteCorrente.nome}` : `Hello, ${utenteCorrente.nome}`;
        document.getElementById('welcomeMsg').innerText = msg;
    }
}

// Helper per ottenere testo nel JS (per gli alert)
function getTesto(key) {
    return traduzioni[linguaCorrente][key] || key;
}
// --- 1. LOGIN & SESSIONE ---
function eseguiLogin() {
    const nome = document.getElementById('loginNome').value;
    const pass = document.getElementById('loginPwd').value;
    const ruolo = document.querySelector('input[name="ruoloLogin"]:checked').value;

    const datiLogin = { nome: nome, password: pass, ruolo: ruolo };

    fetch('/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datiLogin)
    })
    .then(res => {
        if (res.ok) return res.json();
        throw new Error("Credenziali errate o utente non trovato");
    })
    .then(user => {
        utenteCorrente = user;
        avviaSessione();
    })
    .catch(err => alert("Errore Login: " + err + "\nControlla che i dati siano esatti (Case Sensitive)"));
}

function avviaSessione() {
    document.getElementById('login-section').classList.remove('active');
    document.getElementById('mainNav').classList.remove('d-none');
    document.getElementById('welcomeMsg').innerText = `Ciao, ${utenteCorrente.nome}`;

    // --- LOGICA BIBLIOTECARIO ---
    if (utenteCorrente.ruolo === 'BIBLIOTECARIO') {
        // Mostra tutto ciò che è per admin
        document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'block');
        // Mostra i link nella navbar
        document.getElementById('nav-utenti').style.display = 'block';
        document.getElementById('nav-prestiti').style.display = 'block';
    } else {
        // Nascondi tutto ai clienti
        document.querySelectorAll('.admin-only').forEach(el => el.style.display = 'none');
        document.getElementById('nav-utenti').style.display = 'none';
        document.getElementById('nav-prestiti').style.display = 'none';
    }

    showSection('libri');
}

function showSection(id) {
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.getElementById(id + '-section').classList.add('active');
    
    if(id === 'libri') caricaLibri();
    if(id === 'utenti') caricaUtenti();
    if(id === 'prestiti') caricaPrestiti();
}

function logout() { location.reload(); }


// --- 2. GESTIONE LIBRI ---
function caricaLibri() {
    fetch('/api/libri').then(r => r.json()).then(data => {
        const tbody = document.getElementById('tabella-libri');
        tbody.innerHTML = '';
        data.forEach(l => {
            let pulsanti = '';
            if(utenteCorrente && utenteCorrente.ruolo === 'BIBLIOTECARIO') {
                pulsanti = `
                    <button onclick='preparaModificaLibro(${JSON.stringify(l)})' class='btn btn-warning btn-sm'>✏️</button>
                    <button onclick='eliminaLibro(${l.libro_id})' class='btn btn-danger btn-sm'>🗑️</button>
                `;
            }
            tbody.innerHTML += `<tr>
                <td>${l.libro_id}</td>
                <td>${l.titolo}</td>
                <td>${l.autore}</td>
                <td>${l.genere_libro}</td>
                <td>${pulsanti}</td>
            </tr>`;
        });
    });
}

function preparaModificaLibro(libro) {
    document.getElementById('l_titolo').value = libro.titolo;
    document.getElementById('l_autore').value = libro.autore;
    document.getElementById('l_isbn').value = libro.isbn;
    document.getElementById('l_anno').value = libro.anno;
    document.getElementById('l_genere').value = libro.genere_libro;
    
    idModificaLibro = libro.libro_id;
    const btn = document.getElementById('btn-salva-libro');
    btn.innerText = "Modifica Libro";
    btn.classList.replace('btn-success', 'btn-warning');
}

function salvaLibro() {
    const dati = {
        titolo: document.getElementById('l_titolo').value,
        autore: document.getElementById('l_autore').value,
        isbn: document.getElementById('l_isbn').value,
        anno: parseInt(document.getElementById('l_anno').value),
        genere_libro: document.getElementById('l_genere').value
    };

    let url = '/api/libri';
    let metodo = 'POST';

    if (idModificaLibro != null) {
        url = '/api/libri/' + idModificaLibro;
        metodo = 'PUT';
    }

    fetch(url, { method: metodo, body: JSON.stringify(dati) })
        .then(() => { 
            alert(idModificaLibro ? 'Libro Modificato!' : 'Libro Creato!');
            pulisciformLibro();
            caricaLibri(); 
        });
}

function pulisciformLibro() {
    idModificaLibro = null;
    document.querySelectorAll('#libri-section input').forEach(i => i.value = '');
    const btn = document.getElementById('btn-salva-libro');
    btn.innerText = "Salva";
    btn.classList.replace('btn-warning', 'btn-success');
}

function eliminaLibro(id) {
    if(confirm('Eliminare?')) fetch('/api/libri/'+id, { method: 'DELETE' }).then(caricaLibri);
}


// --- 3. GESTIONE UTENTI ---
function caricaUtenti() {
    fetch('/api/utenti').then(r => r.json()).then(data => {
        const tbody = document.getElementById('tabella-utenti');
        tbody.innerHTML = '';
        data.forEach(u => {
            tbody.innerHTML += `<tr>
                <td>${u.utente_id}</td>
                <td>${u.nome} ${u.cognome}</td>
                <td>${u.ruolo}</td>
                <td>
                    <button onclick='preparaModificaUtente(${JSON.stringify(u)})' class='btn btn-warning btn-sm'>✏️</button>
                    <button onclick='eliminaUtente(${u.utente_id})' class='btn btn-danger btn-sm'>🗑️</button>
                </td>
            </tr>`;
        });
    });
}

function preparaModificaUtente(u) {
    document.getElementById('u_nome').value = u.nome;
    document.getElementById('u_cognome').value = u.cognome;
    document.getElementById('u_genere').value = u.genere;
    document.getElementById('u_eta').value = u.eta;
    document.getElementById('u_ruolo').value = u.ruolo;

    idModificaUtente = u.utente_id;
    const btn = document.getElementById('btn-salva-utente');
    btn.innerText = "Modifica Utente";
    btn.classList.replace('btn-success', 'btn-warning');
}

function salvaUtente() {
    const dati = {
        nome: document.getElementById('u_nome').value,
        cognome: document.getElementById('u_cognome').value,
        genere: document.getElementById('u_genere').value,
        eta: parseInt(document.getElementById('u_eta').value),
        ruolo: document.getElementById('u_ruolo').value,
        password: "1234" 
    };

    let url = '/api/utenti';
    let metodo = 'POST';

    if (idModificaUtente != null) {
        url = '/api/utenti/' + idModificaUtente;
        metodo = 'PUT';
    }

    fetch(url, { method: metodo, body: JSON.stringify(dati) })
        .then(() => { 
            alert('Fatto!');
            idModificaUtente = null;
            const btn = document.getElementById('btn-salva-utente');
            btn.innerText = "Crea";
            btn.classList.replace('btn-warning', 'btn-success');
            document.querySelectorAll('#utenti-section input').forEach(i => i.value = '');
            caricaUtenti(); 
        });
}

function eliminaUtente(id) {
    if(confirm('Eliminare?')) fetch('/api/utenti/'+id, { method: 'DELETE' }).then(caricaUtenti);
}


// --- 4. PRESTITI (Versione Robusta) ---
function caricaPrestiti() {
    fetch('/api/prestiti')
        .then(r => {
            if (!r.ok) throw new Error("Errore server");
            return r.json();
        })
        .then(data => {
            console.log("Prestiti:", data);
            const tbody = document.getElementById('tabella-prestiti');
            tbody.innerHTML = '';
            
            if (data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center">Nessun prestito trovato</td></tr>';
                return;
            }

            data.forEach(p => {
                // Usa il controllo ?. per evitare crash se utente/libro sono null
                const nomeUtente = p.utente ? `${p.utente.nome} ${p.utente.cognome}` : "Utente Eliminato";
                const titoloLibro = p.libro ? p.libro.titolo : "Libro Eliminato";
                
                tbody.innerHTML += `
                    <tr>
                        <td>${p.prestito_id}</td>
                        <td>${nomeUtente}</td>
                        <td>${titoloLibro}</td>
                        <td>${p.data_prestito}</td>
                        <td>${p.data_restituzione || 'In corso'}</td>
                        <td>
                            <button onclick="eliminaPrestito(${p.prestito_id})" class="btn btn-danger btn-sm">Chiudi</button>
                        </td>
                    </tr>`;
            });
        })
        .catch(e => console.error("Errore:", e));
}

function creaPrestito() {
    const uID = document.getElementById('p_utente_id').value;
    const lID = document.getElementById('p_libro_id').value;
    const data = document.getElementById('p_data').value;

    if(!uID || !lID || !data) return alert("Compila tutti i campi!");

    const nuovo = {
        data_prestito: data,
        data_restituzione: null,
        utente: { utente_id: parseInt(uID) },
        libro: { libro_id: parseInt(lID) }
    };

    fetch('/api/prestiti', { method: 'POST', body: JSON.stringify(nuovo) })
        .then(res => {
            if(res.ok) { 
                alert('Prestito registrato!'); 
                caricaPrestiti(); 
            } else { 
                alert('Errore creazione (ID non validi?)'); 
            }
        });
}

function eliminaPrestito(id) {
    if(confirm('Segnare come restituito/chiuso?')) {
         fetch('/api/prestiti/'+id, { method: 'DELETE' }).then(caricaPrestiti);
    }
}

// --- UTILITY ---
function popolaDati() {
    fetch('/api/setup/popola').then(r => r.text()).then(msg => alert(msg));
}