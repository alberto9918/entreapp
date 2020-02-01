import http from 'http'
import { env, mongo, port, ip, apiRoot, apiBaseUrl } from './config'
import mongoose from './services/mongoose'
import express from './services/express'
import api from './api'

const app = express(apiRoot, api)
const server = http.createServer(app)

mongoose.connect(mongo.uri)
mongoose.Promise = Promise

setImmediate(() => {
  server.listen(port, ip, () => {
    // console.log('apiBaseUrl: ' + apiBaseUrl)
    console.log('Express server listening on http://%s:%d, in %s mode', ip, port, env)
  })
})

export default app
