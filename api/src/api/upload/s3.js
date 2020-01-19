const AWS = require('aws-sdk')

const ID = 'AKIAIHY2MAPS6KWI4VKQ'
const SECRET = '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'

export const BUCKET_NAME = 'entreapp-bucket'

export const s3 = new AWS.S3({
  accessKeyId: ID,
  secretAccessKey: SECRET
})
