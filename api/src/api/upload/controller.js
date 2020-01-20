const multer = require('multer')
const multers3 = require('multer-s3')
const path = require('path')
/*
 Necesitamos un método controlador que podamos invocar desde
 index.js, y que pueda subir a AWS S3 un fichero recibido con una petición POST.

 Hay que añadir también la opción de rescatar el key y devolverlo, para guardarlo en la base de datos.

*/
const AWS = require('aws-sdk')
const AWS_ID = process.env.AWS_ID
const AWS_SECRET = process.env.AWS_SECRET
const AWS_BUCKET_NAME = process.env.AWS_BUCKET_NAME

const s3 = new AWS.S3({
  accessKeyId: AWS_ID,
  secretAccessKey: AWS_SECRET
})

export const uploadS3 = (type) => {
  return multer({
    storage: multers3({
      s3: s3,
      bucket: AWS_BUCKET_NAME,
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
    // console.log(req.file)
    const returnFileKey = req.file.key.replace('/', '-')
    res.status(201).json({
      // key: req.file.key
      key: returnFileKey
    })
  }
}

export const getFile = (req, res, next) => {

  let key
  if (req.params.key.startsWith('pois-imgs-')) {
    key = 'pois-imgs/' + req.params.key.substr(10)
  }

  // const stream = s3.getObject({ Bucket: 'entreapp-bucket', Key: req.params.key }).createReadStream()
  const stream = s3.getObject({ Bucket: 'entreapp-bucket', Key: key }).createReadStream()

  stream.on('error', function (err) {
    // NoSuchKey: The specified key does not exist
    console.error(err)
    res.sendStatus(404)
  })

  stream.pipe(res)
}
