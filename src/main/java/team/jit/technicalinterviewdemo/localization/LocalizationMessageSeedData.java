package team.jit.technicalinterviewdemo.localization;

import java.util.ArrayList;
import java.util.List;

final class LocalizationMessageSeedData {

    private static final List<String> DOCUMENTED_KEYS = List.of(
            "error.book.isbn_duplicate",
            "error.book.not_found",
            "error.book.stale_version",
            "error.data.integrity_violation",
            "error.request.constraint_violation",
            "error.request.invalid",
            "error.request.invalid_parameter",
            "error.request.malformed_body",
            "error.request.method_not_allowed",
            "error.request.missing_header",
            "error.request.missing_parameter",
            "error.request.resource_not_found",
            "error.request.unsupported_media_type",
            "error.request.validation_failed",
            "error.server.internal"
    );

    private static final List<String> SUPPORTED_LANGUAGES = List.of("en", "es", "de", "fr");

    private LocalizationMessageSeedData() {
    }

    static List<String> documentedKeys() {
        return DOCUMENTED_KEYS;
    }

    static List<String> supportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }

    static List<LocalizationMessage> defaultMessages() {
        List<LocalizationMessage> messages = new ArrayList<>();
        addTranslations(
                messages,
                "error.book.isbn_duplicate",
                "A book with the same ISBN already exists.",
                "Ya existe un libro con el mismo ISBN.",
                "Ein Buch mit derselben ISBN existiert bereits.",
                "Un livre avec le meme ISBN existe deja."
        );
        addTranslations(
                messages,
                "error.book.not_found",
                "The requested book was not found.",
                "No se encontro el libro solicitado.",
                "Das angeforderte Buch wurde nicht gefunden.",
                "Le livre demande est introuvable."
        );
        addTranslations(
                messages,
                "error.book.stale_version",
                "The book was modified by another request. Retry with the latest version.",
                "El libro fue modificado por otra solicitud. Reintente con la version mas reciente.",
                "Das Buch wurde von einer anderen Anfrage geaendert. Wiederholen Sie den Vorgang mit der neuesten Version.",
                "Le livre a ete modifie par une autre requete. Reessayez avec la version la plus recente."
        );
        addTranslations(
                messages,
                "error.data.integrity_violation",
                "The request data violates a database constraint.",
                "Los datos de la solicitud violan una restriccion de la base de datos.",
                "Die Anfragedaten verletzen eine Datenbankeinschraenkung.",
                "Les donnees de la requete violent une contrainte de base de donnees."
        );
        addTranslations(
                messages,
                "error.request.constraint_violation",
                "Request validation constraints were violated.",
                "Se violaron restricciones de validacion de la solicitud.",
                "Validierungsregeln der Anfrage wurden verletzt.",
                "Des contraintes de validation de la requete ont ete violees."
        );
        addTranslations(
                messages,
                "error.request.invalid",
                "The request is invalid.",
                "La solicitud no es valida.",
                "Die Anfrage ist ungueltig.",
                "La requete est invalide."
        );
        addTranslations(
                messages,
                "error.request.invalid_parameter",
                "A request parameter has an invalid value.",
                "Un parametro de la solicitud tiene un valor invalido.",
                "Ein Anfrageparameter hat einen ungueltigen Wert.",
                "Un parametre de requete a une valeur invalide."
        );
        addTranslations(
                messages,
                "error.request.malformed_body",
                "The request body is missing or malformed.",
                "El cuerpo de la solicitud falta o es invalido.",
                "Der Anfrageinhalt fehlt oder ist fehlerhaft.",
                "Le corps de la requete est absent ou mal forme."
        );
        addTranslations(
                messages,
                "error.request.method_not_allowed",
                "The HTTP method is not allowed for this endpoint.",
                "El metodo HTTP no esta permitido para este endpoint.",
                "Die HTTP-Methode ist fuer diesen Endpunkt nicht erlaubt.",
                "La methode HTTP n'est pas autorisee pour ce point de terminaison."
        );
        addTranslations(
                messages,
                "error.request.missing_header",
                "A required request header is missing.",
                "Falta un encabezado obligatorio de la solicitud.",
                "Ein erforderlicher Anfrageheader fehlt.",
                "Un en-tete requis de la requete est manquant."
        );
        addTranslations(
                messages,
                "error.request.missing_parameter",
                "A required request parameter is missing.",
                "Falta un parametro obligatorio de la solicitud.",
                "Ein erforderlicher Anfrageparameter fehlt.",
                "Un parametre requis de la requete est manquant."
        );
        addTranslations(
                messages,
                "error.request.resource_not_found",
                "The requested resource was not found.",
                "No se encontro el recurso solicitado.",
                "Die angeforderte Ressource wurde nicht gefunden.",
                "La ressource demandee est introuvable."
        );
        addTranslations(
                messages,
                "error.request.unsupported_media_type",
                "The provided content type is not supported.",
                "El tipo de contenido proporcionado no es compatible.",
                "Der angegebene Inhaltstyp wird nicht unterstuetzt.",
                "Le type de contenu fourni n'est pas pris en charge."
        );
        addTranslations(
                messages,
                "error.request.validation_failed",
                "Request body validation failed.",
                "La validacion del cuerpo de la solicitud fallo.",
                "Die Validierung des Anfrageinhalts ist fehlgeschlagen.",
                "La validation du corps de la requete a echoue."
        );
        addTranslations(
                messages,
                "error.server.internal",
                "An unexpected error occurred.",
                "Ocurrio un error inesperado.",
                "Ein unerwarteter Fehler ist aufgetreten.",
                "Une erreur inattendue s'est produite."
        );
        return List.copyOf(messages);
    }

    private static void addTranslations(
            List<LocalizationMessage> messages,
            String messageKey,
            String english,
            String spanish,
            String german,
            String french
    ) {
        messages.add(new LocalizationMessage(messageKey, "en", english, description(messageKey, "en")));
        messages.add(new LocalizationMessage(messageKey, "es", spanish, description(messageKey, "es")));
        messages.add(new LocalizationMessage(messageKey, "de", german, description(messageKey, "de")));
        messages.add(new LocalizationMessage(messageKey, "fr", french, description(messageKey, "fr")));
    }

    private static String description(String messageKey, String language) {
        return "Seed translation for %s in %s.".formatted(messageKey, language);
    }
}
