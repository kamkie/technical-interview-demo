do $$
begin
    if exists (
        select 1
        from categories
        group by lower(name)
        having count(*) > 1
    ) then
        raise exception 'Cannot add case-insensitive category uniqueness: duplicate normalized category names exist.';
    end if;
end $$;

create unique index if not exists uk_categories_name_lower
    on categories ((lower(name)));
