import { Router } from 'express'
import { middleware as query } from 'querymen'
import { middleware as body } from 'bodymen'
import { password as passwordAuth, master, token } from '../../services/passport'
import { index, showMe, show, create, update, updatePassword, updateRole, destroy, obtainRoles, allUsersAndFriended, editPoiFavs, editUserFriends } from './controller'
import { schema } from './model'
export User, { schema } from './model'

const router = new Router()
const { email, password, name, picture, images, invalidImages, role, city, language, likes, favs, friends } = schema.tree

/**
 * @api {get} /users Retrieve users
 * @apiName RetrieveUsers
 * @apiGroup User
 * @apiPermission admin
 * @apiParam {String} access_token User access_token.
 * @apiUse listParams
 * @apiSuccess {Object[]} users List of users.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 401 Admin access only.
 */
router.get('/',
  token({ required: true }),
  query(),
  index)
/**
 * @api {get} /users/friended Retrieve users and set the value friended depending on whether its a user friend
 * @apiName RetrieveFriendedUsers
 * @apiGroup User
 * @apiPermission user
 * @apiParam {String} access_token user access token.
 * @apiSuccess {Number} count Total amount of users.
 * @apiSuccess {Object[]} rows List of users.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 401 user access only.
 */
router.get('/friended',
  token({required: true}),
  allUsersAndFriended)
/**
 * @api {get} /users/roles Retrieve all roles
 * @apiName RetrieveRoles
 * @apiGroup User
 * @apiPermission admin
 * @apiParam {String} access_token User access_token.
 * @apiSuccess {Object[]} List of roles.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 401 Admin access only.
 */
router.get('/roles',
  token({ required: true, roles: ['admin'] }),
  obtainRoles)
/**
 * @api {get} /users/me Retrieve current user
 * @apiName RetrieveCurrentUser
 * @apiGroup User
 * @apiPermission user
 * @apiParam {String} access_token User access_token.
 * @apiSuccess {Object} user User's data.
 */
router.get('/me',
  token({ required: true }),
  showMe)
/**
 * @api {put} /users/editUserFriend/:id Update list of user friends
 * @apiName UpdateListUserFriends
 * @apiGroup User
 * @apiPermission user
 * @apiParam {String} access_token admin access token.
 * @apiSuccess {Object} user Users's data.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 404 User not found.
 * @apiError 401 user access only.
 */
router.put('/editUserFriend/:id',
  token({ required: true }),
  editUserFriends)

/**
 * @api {get} /users/:id Retrieve user
 * @apiName RetrieveUser
 * @apiGroup User
 * @apiPermission public
 * @apiSuccess {Object} user User's data.
 * @apiError 404 User not found.
 */
router.get('/:id',
  token({ required: true, roles: ['admin', 'user'] }),
  show)

/**
 * @api {post} /users Create user
 * @apiName CreateUser
 * @apiGroup User
 * @apiPermission master
 * @apiParam {String} access_token Master access_token.
 * @apiParam {String} email User's email.
 * @apiParam {String{6..}} password User's password.
 * @apiParam {String} [name] User's name.
 * @apiParam {String} [picture] User's picture.
 * @apiParam {String=user,admin} [role=user] User's role.
 * @apiSuccess (Sucess 201) {Object} user User's data.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 401 Master access only.
 * @apiError 409 Email already registered.
 */
router.post('/',
  master(),
  body({ email, password, name, picture, role, language }),
  create)

/**
 * @api {put} /users/:id Update user
 * @apiName UpdateUser
 * @apiGroup User
 * @apiPermission user
 * @apiParam {String} access_token User access_token.
 * @apiParam {String} [name] User's name.
 * @apiParam {String} [picture] User's picture.
 * @apiSuccess {Object} user User's data.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 401 Current user or admin access only.
 * @apiError 404 User not found.
 */
router.put('/:id',
  token({ required: true }),
  body({email, name, city, language, picture,images,invalidImages, likes, favs, friends}),
  update)
/**
 * @api {put} /users/editRole/:id Update role
 * @apiName UpdateRole
 * @apiGroup User
 * @apiPermission admin
 * @apiParam {String} access_token admin access token.
 * @apiParam role Role's name.
 * @apiSuccess {Object} role Role's data.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 404 role not found.
 * @apiError 401 admin access only.
 */
router.put('/editRole/:id',
  token({ required: true, role: ['admin']}),
  body({role}),
  updateRole)
//   body({ name, email, password, city, languaje }),

/**
 * @api {put} /users/:id/password Update password
 * @apiName UpdatePassword
 * @apiGroup User
 * @apiHeader {String} Authorization Basic authorization with email and password.
 * @apiParam {String{6..}} password User's new password.
 * @apiSuccess (Success 201) {Object} user User's data.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 401 Current user access only.
 * @apiError 404 User not found.
 */
router.put('/:id/password',
  passwordAuth(),
  body({ password }),
  updatePassword)

/**
 * @api {delete} /users/:id Delete user
 * @apiName DeleteUser
 * @apiGroup User
 * @apiPermission admin
 * @apiParam {String} access_token User access_token.
 * @apiSuccess (Success 204) 204 No Content.
 * @apiError 401 Admin access only.
 * @apiError 404 User not found.
 */
router.delete('/:id',
  token({ required: true, roles: ['admin'] }),
  destroy)

  /**
 * @api {put} /users/editPoiFav/:id Update poi fav
 * @apiName UpdatePoiFav
 * @apiGroup User
 * @apiPermission user
 * @apiParam {String} access_token admin access token.
 * @apiSuccess {Object} user Users's data.
 * @apiError {Object} 400 Some parameters may contain invalid values.
 * @apiError 404 User not found.
 * @apiError 401 user access only.
 */
router.put('/editPoiFav/:id',
  token({ required: true }),
  editPoiFavs)

export default router
