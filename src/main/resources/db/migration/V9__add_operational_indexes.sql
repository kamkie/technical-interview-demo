create
extension if not exists pg_trgm;

create index if not exists idx_books_title_trgm
    on books using gin (lower (title) gin_trgm_ops);

create index if not exists idx_books_author_trgm
    on books using gin (lower (author) gin_trgm_ops);

create index if not exists idx_books_isbn_trgm
    on books using gin (lower (isbn) gin_trgm_ops);

create index if not exists idx_book_categories_category_id
    on book_categories (category_id);

create index if not exists idx_categories_name_lower
    on categories ((lower (name)));

create index if not exists idx_audit_logs_actor_login
    on audit_logs (actor_login);

create index if not exists idx_audit_logs_action
    on audit_logs (action);
