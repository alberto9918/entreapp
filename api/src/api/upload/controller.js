const multer = require('multer')
const multers3 = require('multer-s3')
const path = require('path')
// const s3 = require('./s3')
/*
 Necesitamos un método controlador que podamos invocar desde
 index.js, y que pueda subir a AWS S3 un fichero recibido con una petición POST.

 Hay que añadir también la opción de rescatar el key y devolverlo, para guardarlo en la base de datos.

*/
const AWS = require('aws-sdk')
const ID = 'AKIAIHY2MAPS6KWI4VKQ'
const SECRET = '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'

// const BUCKET_NAME = 'entreapp-bucket'

const s3 = new AWS.S3({
  accessKeyId: ID,
  secretAccessKey: SECRET
})

export const uploadS3 = (type) => {
  return multer({
    storage: multers3({
      s3: s3,
      bucket: 'entreapp-bucket',
      // acl: 'public-read',
      contentType: multers3.AUTO_CONTENT_TYPE,
      key: function (req, file, cb) {
      // Fecha y hora en milisegundos + nombre del fichero + un número aleatorio
        const fileName = file.originalname
        const fechaHora = Date.now()
        const nombreFichero = path.parse(fileName).name
        const extension = path.parse(fileName).ext
        const randomNumber = Math.floor(Math.random() * (1000 - 0) + 0)
        const fileKey = type + '/' + fechaHora + '_' + nombreFichero + randomNumber + extension
        cb(null, fileKey)
      }
    })
  })
}
export const postUploadS3 = (req, res, next) => {
  if (!req.file) {
    console.log('Petición UPLOAD sin fichero')
    return res.status(400).json({ success: false })
  } else {
    console.log('Fichero recibido')
    console.log(req.file.key)
    res.status(201).json({ key: req.file.key })
  }
}

export const getFile = (req, res, next) => {
  const stream = s3.getObject({ Bucket: 'entreapp-bucket', Key: req.params.key }).createReadStream()

  stream.on('error', function (err) {
    // NoSuchKey: The specified key does not exist
    console.error(err)
    res.sendStatus(404)
  })

  stream.pipe(res)
}
