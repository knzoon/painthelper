# painthelper
Source code for the web app Painthelper

## Build and deploy

### Environment variables

Docker is needing a `.env` file

```
DB_ROOT_PASSWD=yourSecretPasswordForDBRoot
DB_USER_PASSWD=yourSecretPasswordForDBUser
FEED_READ_DISABLE=false
```

Maven is needing environment variables in the shell

```
$ export DB_USER_PASSWD=yourSecretPasswordForDBUser
$ export export DB_CONNECTION_STRING=jdbc:mariadb://localhost:3306/knzoon
```
The connection string should point to a test instance of db.
Only used for tests while building jar

If you want to configure to read feeds from locolhost you can add this line in the `.env` file

```
FEED_READ_BASEURL=http://localhost:8000/feed/
```
