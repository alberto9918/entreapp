// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  //apiUrl: 'https://ec2-52-15-50-154.us-east-2.compute.amazonaws.com/api',
  apiUrl: 'http://localhost:9000',
  //apiUrl: 'http://www.visiton.eu/api',
  masterKey: '{"access_token": "WYGSKxg0IwVvtZAWjDtVAWfWcbnugIbX"}',
  masterKeyTemporal: 'access_token=WYGSKxg0IwVvtZAWjDtVAWfWcbnugIbX'
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
