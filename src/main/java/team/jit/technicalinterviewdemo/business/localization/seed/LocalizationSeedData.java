package team.jit.technicalinterviewdemo.business.localization.seed;

import java.util.ArrayList;
import java.util.List;
import team.jit.technicalinterviewdemo.business.localization.Localization;

public final class LocalizationSeedData {

    private static final List<String> DOCUMENTED_KEYS = List.of(
            "error.category.in_use",
            "error.category.not_found",
            "error.book.isbn_duplicate",
            "error.book.not_found",
            "error.book.stale_version",
            "error.data.integrity_violation",
            "error.localization.duplicate",
            "error.localization.not_found",
            "error.request.constraint_violation",
            "error.request.csrf_invalid",
            "error.request.forbidden",
            "error.request.invalid",
            "error.request.invalid_parameter",
            "error.request.malformed_body",
            "error.request.method_not_allowed",
            "error.request.missing_header",
            "error.request.missing_parameter",
            "error.request.resource_not_found",
            "error.request.unauthorized",
            "error.request.unsupported_media_type",
            "error.request.validation_failed",
            "error.user.not_found",
            "error.server.internal");

    private static final List<String> SUPPORTED_LANGUAGES = List.of("en", "es", "de", "fr", "pl", "uk", "no");

    private LocalizationSeedData() {}

    public static List<String> documentedKeys() {
        return DOCUMENTED_KEYS;
    }

    public static List<String> supportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }

    public static List<Localization> defaultMessages() {
        List<Localization> messages = new ArrayList<>();
        addTranslations(
                messages,
                "error.category.in_use",
                "The category cannot be deleted because it is still assigned to one or more books.",
                "La categoria no se puede eliminar porque todavia esta asignada a uno o mas libros.",
                "Die Kategorie kann nicht geloescht werden, weil sie noch einem oder mehreren Buechern zugewiesen ist.",
                "La categorie ne peut pas etre supprimee car elle est encore attribuee a un ou plusieurs livres.",
                "Nie mozna usunac kategorii, poniewaz jest nadal przypisana do co najmniej jednej ksiazki.",
                "Katehoriiu nemozhlyvo vydalyty, oskilky vona vse shche pryznachena odnii chy kilkom knyham.",
                "Kategorien kan ikke slettes fordi den fortsatt er tilordnet en eller flere boker.");
        addTranslations(
                messages,
                "error.category.not_found",
                "The requested category was not found.",
                "No se encontro la categoria solicitada.",
                "Die angeforderte Kategorie wurde nicht gefunden.",
                "La categorie demandee est introuvable.",
                "Nie znaleziono zadanej kategorii.",
                "Zapytanu katehoriiu ne znaideno.",
                "Den forespurte kategorien ble ikke funnet.");
        addTranslations(
                messages,
                "error.book.isbn_duplicate",
                "A book with the same ISBN already exists.",
                "Ya existe un libro con el mismo ISBN.",
                "Ein Buch mit derselben ISBN existiert bereits.",
                "Un livre avec le meme ISBN existe deja.",
                "Ksiazka z tym samym numerem ISBN juz istnieje.",
                "Knyha z takym samym ISBN vzhe isnuie.",
                "En bok med samme ISBN finnes allerede.");
        addTranslations(
                messages,
                "error.book.not_found",
                "The requested book was not found.",
                "No se encontro el libro solicitado.",
                "Das angeforderte Buch wurde nicht gefunden.",
                "Le livre demande est introuvable.",
                "Nie znaleziono zadanej ksiazki.",
                "Zapytanu knyhu ne znaideno.",
                "Den forespurte boken ble ikke funnet.");
        addTranslations(
                messages,
                "error.book.stale_version",
                "The book was modified by another request. Retry with the latest version.",
                "El libro fue modificado por otra solicitud. Reintente con la version mas reciente.",
                "Das Buch wurde von einer anderen Anfrage geaendert. Wiederholen Sie den Vorgang mit der neuesten"
                        + " Version.",
                "Le livre a ete modifie par une autre requete. Reessayez avec la version la plus recente.",
                "Ksiazka zostala zmodyfikowana przez inne zadanie. Sprobuj ponownie z najnowsza wersja.",
                "Knyhu zmineno inshym zapytom. Povtorit sprobu z ostannoiu versiieiu.",
                "Boken ble endret av en annen foresporsel. Prov igjen med den nyeste versjonen.");
        addTranslations(
                messages,
                "error.data.integrity_violation",
                "The request data violates a database constraint.",
                "Los datos de la solicitud violan una restriccion de la base de datos.",
                "Die Anfragedaten verletzen eine Datenbankeinschraenkung.",
                "Les donnees de la requete violent une contrainte de base de donnees.",
                "Dane zadania naruszaja ograniczenie bazy danych.",
                "Dani zapytu porushuiut obmezhennia bazy danykh.",
                "Foresporselsdataene bryter en databasebegrensning.");
        addTranslations(
                messages,
                "error.localization.duplicate",
                "A localization message with the same key and language already exists.",
                "Ya existe un mensaje de localizacion con la misma clave y el mismo idioma.",
                "Eine Lokalisierungsnachricht mit demselben Schluessel und derselben Sprache existiert bereits.",
                "Un message de localisation avec la meme cle et la meme langue existe deja.",
                "Komunikat lokalizacyjny z tym samym kluczem i jezykiem juz istnieje.",
                "Povidomlennia lokalizatsii z takym samym kliuchem i movoiu vzhe isnuie.",
                "En lokaliseringsmelding med samme nokkel og sprak finnes allerede.");
        addTranslations(
                messages,
                "error.localization.not_found",
                "The requested localization message was not found.",
                "No se encontro el mensaje de localizacion solicitado.",
                "Die angeforderte Lokalisierungsnachricht wurde nicht gefunden.",
                "Le message de localisation demande est introuvable.",
                "Nie znaleziono zadanego komunikatu lokalizacyjnego.",
                "Zapytane povidomlennia lokalizatsii ne znaideno.",
                "Den forespurte lokaliseringsmeldingen ble ikke funnet.");
        addTranslations(
                messages,
                "error.request.constraint_violation",
                "Request validation constraints were violated.",
                "Se violaron restricciones de validacion de la solicitud.",
                "Validierungsregeln der Anfrage wurden verletzt.",
                "Des contraintes de validation de la requete ont ete violees.",
                "Naruszono ograniczenia walidacji zadania.",
                "Porusheno obmezhennia validatsii zapytu.",
                "Valideringsregler for foresporselen ble brutt.");
        addTranslations(
                messages,
                "error.request.csrf_invalid",
                "A valid CSRF token is required to perform this operation.",
                "Se requiere un token CSRF valido para realizar esta operacion.",
                "Fuer diesen Vorgang ist ein gueltiges CSRF-Token erforderlich.",
                "Un jeton CSRF valide est requis pour effectuer cette operation.",
                "Do wykonania tej operacji wymagany jest prawidlowy token CSRF.",
                "Dlia vykonannia tsiiei operatsii potriben diisnyi token CSRF.",
                "Et gyldig CSRF-token kreves for aa utfore denne operasjonen.");
        addTranslations(
                messages,
                "error.request.forbidden",
                "You do not have permission to perform this operation.",
                "No tiene permiso para realizar esta operacion.",
                "Sie haben keine Berechtigung, diesen Vorgang auszufuehren.",
                "Vous n'avez pas la permission d'effectuer cette operation.",
                "Nie masz uprawnien do wykonania tej operacji.",
                "Vy ne maiete dozvolu vykonuvaty tsiu operatsiiu.",
                "Du har ikke tillatelse til aa utfore denne operasjonen.");
        addTranslations(
                messages,
                "error.request.invalid",
                "The request is invalid.",
                "La solicitud no es valida.",
                "Die Anfrage ist ungueltig.",
                "La requete est invalide.",
                "Zadanie jest nieprawidlowe.",
                "Zapyt ye nevalidnym.",
                "Foresporselen er ugyldig.");
        addTranslations(
                messages,
                "error.request.invalid_parameter",
                "A request parameter has an invalid value.",
                "Un parametro de la solicitud tiene un valor invalido.",
                "Ein Anfrageparameter hat einen ungueltigen Wert.",
                "Un parametre de requete a une valeur invalide.",
                "Parametr zadania ma nieprawidlowa wartosc.",
                "Parametr zapytu maie nepravylne znachennia.",
                "En foresporselsparameter har en ugyldig verdi.");
        addTranslations(
                messages,
                "error.request.malformed_body",
                "The request body is missing or malformed.",
                "El cuerpo de la solicitud falta o es invalido.",
                "Der Anfrageinhalt fehlt oder ist fehlerhaft.",
                "Le corps de la requete est absent ou mal forme.",
                "Brakuje tresci zadania albo ma ona nieprawidlowy format.",
                "Tilo zapytu vidsutnie abo maie nekorektnyi format.",
                "Foresporselskroppen mangler eller har ugyldig format.");
        addTranslations(
                messages,
                "error.request.method_not_allowed",
                "The HTTP method is not allowed for this endpoint.",
                "El metodo HTTP no esta permitido para este endpoint.",
                "Die HTTP-Methode ist fuer diesen Endpunkt nicht erlaubt.",
                "La methode HTTP n'est pas autorisee pour ce point de terminaison.",
                "Metoda HTTP nie jest dozwolona dla tego endpointu.",
                "Metod HTTP ne dozvoleno dlia tsoho endpointu.",
                "HTTP-metoden er ikke tillatt for dette endepunktet.");
        addTranslations(
                messages,
                "error.request.missing_header",
                "A required request header is missing.",
                "Falta un encabezado obligatorio de la solicitud.",
                "Ein erforderlicher Anfrageheader fehlt.",
                "Un en-tete requis de la requete est manquant.",
                "Brakuje wymaganego naglowka zadania.",
                "Vidsutnii oboviazkovyi zaholovok zapytu.",
                "En obligatorisk foresporselsheader mangler.");
        addTranslations(
                messages,
                "error.request.missing_parameter",
                "A required request parameter is missing.",
                "Falta un parametro obligatorio de la solicitud.",
                "Ein erforderlicher Anfrageparameter fehlt.",
                "Un parametre requis de la requete est manquant.",
                "Brakuje wymaganego parametru zadania.",
                "Vidsutnii oboviazkovyi parametr zapytu.",
                "En obligatorisk foresporselsparameter mangler.");
        addTranslations(
                messages,
                "error.request.resource_not_found",
                "The requested resource was not found.",
                "No se encontro el recurso solicitado.",
                "Die angeforderte Ressource wurde nicht gefunden.",
                "La ressource demandee est introuvable.",
                "Nie znaleziono zadanego zasobu.",
                "Zapytanyi resurs ne znaideno.",
                "Den forespurte ressursen ble ikke funnet.");
        addTranslations(
                messages,
                "error.request.unauthorized",
                "You must authenticate before performing this operation.",
                "Debe autenticarse antes de realizar esta operacion.",
                "Sie muessen sich authentifizieren, bevor Sie diesen Vorgang ausfuehren.",
                "Vous devez vous authentifier avant d'effectuer cette operation.",
                "Musisz sie uwierzytelnic przed wykonaniem tej operacji.",
                "Vy maiete avtoryzuvatysia pered vykonanniam tsiiei operatsii.",
                "Du ma autentisere deg for du utforer denne operasjonen.");
        addTranslations(
                messages,
                "error.request.unsupported_media_type",
                "The provided content type is not supported.",
                "El tipo de contenido proporcionado no es compatible.",
                "Der angegebene Inhaltstyp wird nicht unterstuetzt.",
                "Le type de contenu fourni n'est pas pris en charge.",
                "Podany typ zawartosci nie jest obslugiwany.",
                "Nadanyi typ vmistu ne pidtrymuietsia.",
                "Den oppgitte innholdstypen stottes ikke.");
        addTranslations(
                messages,
                "error.request.validation_failed",
                "Request body validation failed.",
                "La validacion del cuerpo de la solicitud fallo.",
                "Die Validierung des Anfrageinhalts ist fehlgeschlagen.",
                "La validation du corps de la requete a echoue.",
                "Walidacja tresci zadania nie powiodla sie.",
                "Validatsiia tila zapytu ne vdalsia.",
                "Validering av foresporselskroppen mislyktes.");
        addTranslations(
                messages,
                "error.user.not_found",
                "The requested user account was not found.",
                "No se encontro la cuenta de usuario solicitada.",
                "Das angeforderte Benutzerkonto wurde nicht gefunden.",
                "Le compte utilisateur demande est introuvable.",
                "Nie znaleziono zadanego konta uzytkownika.",
                "Zapytanyi oblikovyi zapys korystuvacha ne znaideno.",
                "Den forespurte brukerkontoen ble ikke funnet.");
        addTranslations(
                messages,
                "error.server.internal",
                "An unexpected error occurred.",
                "Ocurrio un error inesperado.",
                "Ein unerwarteter Fehler ist aufgetreten.",
                "Une erreur inattendue s'est produite.",
                "Wystapil nieoczekiwany blad.",
                "Stalasia neochikuvana pomylka.",
                "Det oppstod en uventet feil.");
        return List.copyOf(messages);
    }

    private static void addTranslations(
            List<Localization> messages,
            String messageKey,
            String english,
            String spanish,
            String german,
            String french,
            String polish,
            String ukrainian,
            String norwegian) {
        messages.add(new Localization(messageKey, "en", english, description(messageKey, "en")));
        messages.add(new Localization(messageKey, "es", spanish, description(messageKey, "es")));
        messages.add(new Localization(messageKey, "de", german, description(messageKey, "de")));
        messages.add(new Localization(messageKey, "fr", french, description(messageKey, "fr")));
        messages.add(new Localization(messageKey, "pl", polish, description(messageKey, "pl")));
        messages.add(new Localization(messageKey, "uk", ukrainian, description(messageKey, "uk")));
        messages.add(new Localization(messageKey, "no", norwegian, description(messageKey, "no")));
    }

    private static String description(String messageKey, String language) {
        return "Seed translation for %s in %s.".formatted(messageKey, language);
    }
}
