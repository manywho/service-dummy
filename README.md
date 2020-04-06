ManyWho Dummy Service
======================

[![Build Status](https://travis-ci.org/manywho/service-dummy.svg?branch=develop)](https://travis-ci.org/manywho/service-dummy)
[![Docker Automated build](https://img.shields.io/docker/automated/manywho/service-dummy.svg?maxAge=2592000)](https://hub.docker.com/r/manywho/service-dummy)
[![Docker Pulls](https://img.shields.io/docker/pulls/manywho/service-dummy.svg?maxAge=2592000)](https://hub.docker.com/r/manywho/service-dummy)

This miniature service is used to for stubbing integrations in Flows, and doesn't really do anything of value.

The service always login the user as user1 when using the identity functionality, the user1 is member of group1.

## Running

The service is compatible with Heroku, and can be deployed by clicking the button below:

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy?template=https://github.com/manywho/service-dummy/tree/develop)

The service can be started with this following optional arguments. If all arguments are ommitted, the service defaults to starting a V1 instance over http.

`service-dummy.jar [v2] [keystore-path] [truststore-path]`

The keystore is used to identify the server during https communication. The truststore is used to determine trusted clients. 

### Examples;

V1 over http `service-dummy.jar`
V2 over https `service-dummy.jar true /path/to/server_keystore.jks`
V2 over https with client cert auth `service-dummy.jar true /path/to/server_keystore.jks /path/to/server_truststore.jks` 

Some basic self-signed certs are packaged in `src/main/resources`. If starting the service with these, the `client_keystore.jks` will need to be passed by calling clients to authenticate.

## Contributing

Contributions are welcome to the project - whether they are feature requests, improvements or bug fixes! Refer to 
[CONTRIBUTING.md](CONTRIBUTING.md) for our contribution requirements.

## License

This service is released under the [MIT License](http://opensource.org/licenses/mit-license.php).
