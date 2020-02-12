import { Router } from 'express'
import user from './user'
import auth from './auth'
import poi from './poi'
import comment from './comment'
import badge from './badge'
import category from './category'
import route from './route'
import upload from './upload'
import language from './language'
import { token } from '../services/passport'
import rating from './rating'
require('dotenv').config()
const router = new Router()

/**
 * @apiDefine master Master access only
 * You must pass `access_token` parameter or a Bearer Token authorization header
 * to access this endpoint.
 */
/**
 * @apiDefine admin Admin access only
 * You must pass `access_token` parameter or a Bearer Token authorization header
 * to access this endpoint.
 */
/**
 * @apiDefine user User access only
 * You must pass `access_token` parameter or a Bearer Token authorization header
 * to access this endpoint.
 */
/**
 * @apiDefine listParams
 * @apiParam {String} [q] Query to search.
 * @apiParam {Number{1..30}} [page=1] Page number.
 * @apiParam {Number{1..100}} [limit=30] Amount of returned items.
 * @apiParam {String[]} [sort=-createdAt] Order of returned items.
 * @apiParam {String[]} [fields] Fields to be returned.
 */
router.use('/users', user)
router.use('/auth', auth)
router.use('/pois', poi)
router.use('/comments', comment)
router.use('/badges', badge)
router.use('/categories', category)
router.use('/routes', route)
router.use('/files', upload)
router.use('/languages', language)
router.use('/ratings', rating)

router.use('/auth/check/token/', token({required: true}), (req, res, next) => {
  res.status(200).end()
})

export default router
