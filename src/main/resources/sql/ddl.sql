create table member (
    id uuid not null,
    name varchar(255),
    primary key (id)
);

-- sample data
INSERT INTO entitymanager.member (id, name) VALUES ('b0131310-42d8-4fc5-a123-273e01467268', '깅원화');
INSERT INTO entitymanager.member (id, name) VALUES ('2e61abf6-3312-45d2-9e48-1e105a7fba0d', '오동규');
INSERT INTO entitymanager.member (id, name) VALUES ('91905d95-5161-4246-805e-d50960bef07b', '이영천');
INSERT INTO entitymanager.member (id, name) VALUES ('3f30bd6d-2630-4098-8e94-238d18df9013', '강원천');
-- INSERT INTO entitymanager.member (id, name) VALUES ('40d9f800-e64b-49f9-8654-a46b4d177af6', '권다애');

