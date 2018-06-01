-- These definitions are copied from:
-- http://docs.spring.io/spring-security/site/docs/4.0.0.CI-SNAPSHOT/reference/htmlsingle/#appendix-schema

\set security_schema 'spring_security'
DROP SCHEMA IF EXISTS :security_schema CASCADE;
CREATE SCHEMA :security_schema;
SET search_path=:security_schema;

-- alter role spring_server in database assets set search_path=spring_security;

-- User schema:

CREATE TABLE users(

    username VARCHAR(256) NOT NULL PRIMARY KEY
   ,password VARCHAR(256) NOT NULL
   ,enabled  BOOLEAN      NOT NULL

)
;

CREATE UNIQUE INDEX users_username_uk ON users(username)
;

COMMENT ON TABLE users IS 'Used by Spring Security for authenticating users who have accounts on the application.';


CREATE TABLE authorities (

    username  VARCHAR(256) NOT NULL
   ,authority VARCHAR(256) NOT NULL

   ,CONSTRAINT authorities_username_fk FOREIGN KEY(username) REFERENCES users(username)

)
;

CREATE UNIQUE INDEX authorities_username_authority_uk ON authorities(username,authority)
;

COMMENT ON TABLE authorities IS 'Contains the list of Roles associated with a user.';


-- Group authorities:

CREATE TABLE groups (
   id         SERIAL PRIMARY KEY
  ,group_name VARCHAR(256) NOT NULL
)
;

--COMMENT ON TABLE groups IS '';


CREATE TABLE group_authorities (

    group_id  SERIAL PRIMARY KEY
   ,authority VARCHAR(256) NOT NULL

   ,CONSTRAINT group_authorities_group_id_fk FOREIGN KEY(group_id) REFERENCES groups(id)

)
;


CREATE TABLE group_members (

    id SERIAL PRIMARY KEY
   ,username  VARCHAR(256) NOT NULL
   ,group_id  BIGINT NOT NULL

   ,CONSTRAINT group_members_group_id_fk FOREIGN KEY(group_id) REFERENCES groups(id)
)
;

-- Remember me

CREATE TABLE persistent_logins (
    username  VARCHAR(256) NOT NULL
   ,series    VARCHAR(256) PRIMARY KEY
   ,token     VARCHAR(256) NOT NULL
   ,last_used TIMESTAMP    NOT NULL
)
;


-- ACL

CREATE TABLE acl_sid(

    id        BIGSERIAL    NOT NULL primary key
   ,principal BOOLEAN      NOT NULL
   ,sid       VARCHAR(100) NOT NULL

   ,CONSTRAINT acl_sid_sid_principal_uk UNIQUE(sid, principal)
)
;


CREATE TABLE acl_class(

    id    BIGSERIAL    NOT NULL PRIMARY KEY
   ,class VARCHAR(100) NOT NULL

   ,CONSTRAINT acl_class_class_uk UNIQUE(class)
)
;


CREATE TABLE acl_object_identity(

    id                 BIGSERIAL PRIMARY KEY
   ,object_id_class    BIGINT NOT NULL
   ,object_id_identity BIGINT NOT NULL
   ,parent_object      BIGINT
   ,owner_sid          BIGINT
   ,entries_inheriting BOOLEAN NOT NULL

   ,CONSTRAINT acl_object_identity_class_identity_uk UNIQUE(object_id_class, object_id_identity)
   ,CONSTRAINT acl_object_identity_parent_fk FOREIGN KEY(parent_object)   REFERENCES acl_object_identity(id)
   ,CONSTRAINT acl_object_identity_object_fk FOREIGN KEY(object_id_class) REFERENCES acl_class(id)
   ,CONSTRAINT acl_object_identity_owner_fk  FOREIGN KEY(owner_sid)       REFERENCES acl_sid(id)

)
;


CREATE TABLE acl_entry(

    id                  BIGSERIAL PRIMARY KEY
   ,acl_object_identity BIGINT NOT NULL
   ,ace_order           INT NOT NULL
   ,sid                 BIGINT NOT NULL
   ,mask                INTEGER NOT NULL
   ,granting            BOOLEAN NOT NULL
   ,audit_success       BOOLEAN NOT NULL
   ,audit_failure       BOOLEAN NOT NULL

   ,CONSTRAINT acl_entry_object_order_uk UNIQUE(acl_object_identity, ace_order)
   ,CONSTRAINT acl_entry_object_fk FOREIGN KEY(acl_object_identity) REFERENCES acl_object_identity(id)
   ,CONSTRAINT acl_entry_sid_fk    FOREIGN KEY(sid)                 REFERENCES acl_sid(id)

)
;


--------------------------------------
-- Enhancements to Spring definitions
--------------------------------------

CREATE TABLE tenant(
    tenant         VARCHAR(256) NOT NULL PRIMARY KEY
   ,organization   VARCHAR(256) NOT NULL
)
;

COMMENT ON TABLE tenant IS 'Description of shared data groups.';

COMMENT ON COLUMN tenant.tenant IS 'Schema for shared data group.';
COMMENT ON COLUMN tenant.organization IS 'Name of the group sharing data.';


CREATE TYPE user_account_status AS ENUM('ACTIVE', 'LOCKED', 'RETIRED');

CREATE TABLE user_app_details(
    username       VARCHAR(256) NOT NULL PRIMARY KEY
   ,status         user_account_status  NOT NULL DEFAULT 'ACTIVE'::user_account_status
   ,tenant         VARCHAR(64)  NOT NULL
   ,display_name   VARCHAR(64)  NOT NULL
   ,email          VARCHAR(64)  NOT NULL

   ,CONSTRAINT user_app_details_username_fk FOREIGN KEY(username) REFERENCES users(username)
   ,CONSTRAINT tenant_tenant_fk FOREIGN KEY(tenant) REFERENCES tenant(tenant)
)
;

COMMENT ON TABLE user_app_details IS 'Enhance spring users table with application-specific details.';

COMMENT ON COLUMN user_app_details.username IS 'The name of the user account used for login purposes';
COMMENT ON COLUMN user_app_details.status IS 'The status of an account will affect the operations it may perform. An account my be "ACTIVE", "LOCKED", "RETIRED", etc';
COMMENT ON COLUMN user_app_details.tenant IS 'The identifier';
COMMENT ON COLUMN user_app_details.display_name IS 'The name to use for display or greeting purposes.';
COMMENT ON COLUMN user_app_details.email IS 'An address to which notifications may be sent.';



SET search_path TO DEFAULT;
