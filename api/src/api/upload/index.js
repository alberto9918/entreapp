// server.js
import { Router } from 'express'
import { uploadS3, postUploadS3, getFile, deleteFile, hola } from './controller'
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

/**
 * @api {post} /upload/image Upload an image
 * @apiName UploadImage
 * @apiGroup Upload
 * @apiPermission user
 * @apiParam {String} master() user access token.
 * @apiParam {File} uploadS3('pois-imgs').single('photo') image to be uploaded.
 * @apiSuccess {Object} fileKey Image's key.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 404 Route not found.
 * @apiError 401 user access only.
 */
router.post('/upload/image', master(), uploadS3('pois-imgs').single('photo'), postUploadS3)

/**
 * @api {post} /upload/audio Upload an audio
 * @apiName UploadAudio
 * @apiGroup Upload
 * @apiPermission user
 * @apiParam {String} master() user access token.
 * @apiParam {File} uploadS3('pois-aud').single('audio') audio to be uploaded.
 * @apiSuccess {Object} key Audio's key.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 404 Route not found.
 * @apiError 401 user access only.
 */

router.post('/upload/audio', master(), uploadS3('pois-aud').single('audio'), postUploadS3)

/**
 * @api {post} /upload/avatar Upload an avatar
 * @apiName UploadAvatar
 * @apiGroup Upload
 * @apiPermission user
 * @apiParam {String} master() user access token.
 * @apiParam {File} master(), uploadS3('avatar').single('photo') avatar to be uploaded.
 * @apiSuccess {Object} key Avatar's key.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 404 Route not found.
 * @apiError 401 user access only.
 */

router.post('/upload/avatar', master(), uploadS3('avatar').single('photo'), postUploadS3)

/**
 * @api {get} /:key Get a file
 * @apiName GetFile
 * @apiGroup Upload
 * @apiPermission user
 * @apiParam key Name of the file as it's stored.
 * @apiSuccess {String} Url of the file.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 404 Route not found.
 */

router.get('/:key', getFile)

/**
 * @api {delete} /:key Delete a file
 * @apiName DeleteFile
 * @apiGroup Upload
 * @apiPermission user
 * @apiParam {String} master() user access token.
 * @apiParam key Name of the file as it's stored.
 * @apiSuccess {String} Url of the file.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 404 Route not found.
 */

router.delete('/:key', master(), deleteFile);

export default router
