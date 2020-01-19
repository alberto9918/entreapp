// server.js
import { Router } from 'express'
import { uploadS3, postUploadS3, getFile } from './controller'
import { master } from '../../services/passport'
/*const path = require('path')
const multer = require('multer')
const bodyParser = require('body-parser')
*/
const router = new Router()
/*
const DIR = '../servidor-archivos'

let storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, DIR)
  },
  filename: (req, file, cb) => {
    cb(null, file.fieldname + '-' + Date.now() + path.extname(file.originalname));
  }
})

let upload = multer({storage: storage})

router.use(bodyParser.json())
router.use(bodyParser.urlencoded({extended: true}))

router.use(function (req, res, next) {
  res.setHeader('Access-Control-Allow-Origin', 'http://localhost:4200')
  res.setHeader('Access-Control-Allow-Methods', 'POST')
  res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type')
  res.setHeader('Access-Control-Allow-Credentials', true)
  next()
})

router.post('/', upload.single('photo'), function (req, res) {
  if (!req.file) {
    console.log('No file received')
    return res.send({ success: false })
  } else {
    console.log('file received');
    return res.send({
      success: true,
      file: req.file
    })
  }
})
*/
router.post('/upload/image', master(), uploadS3('pois-imgs').single('photo'), postUploadS3)
router.post('/upload/audio', master(), uploadS3('pois-aud').single('photo'), postUploadS3)
router.post('/upload/avatar', master(), uploadS3('pois-aud').single('photo'), postUploadS3)

router.get('/:key', master(), getFile)
export default router
