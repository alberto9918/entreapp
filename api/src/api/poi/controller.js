import _ from 'lodash'
import QRCode from 'qrcode'

import { Poi } from '.'
import mongoose from '../../services/mongoose'
import { notFound, success } from '../../services/response/'
import { Badge } from '../badge'
import { apiBaseUrl } from '../../config'

/** Create a new POI */
/*
export const create = ({ bodymen: { body } }, res, next) => {
  // body.coverImage = body.images[0];
  Poi.create(body)
    .then((poi) => {
      poi.view(2)
      var opts = {
        color: {
          dark: '#000', // Black dots
          light: '#0000' // Transparent background
        },
        type: 'svg'
      }
      // QRCode.toString(`https://entreapp.herokuapp.com/pois/visit/${poi.id}`, opts).then(string => {
      QRCode.toString(`http://www.visiton.eu/api/pois/visit/${poi.uniqueName}`, opts).then(string => {
        // Poi.findByIdAndUpdate({ _id: poi.id }, { $set: { qrCode: string.split('\n')[0] } }, { new: true }).then(success(res, 200)).catch(next)
        Poi.findOneAndUpdate({ uniqueName: poi.uniqueName }, { $set: { qrCode: `http://www.visiton.eu/api/pois/qr/${poi.uniqueName}` } }, { new: true }).then(success(res, 200)).catch(next)
      }).then(success(res, 200)).catch(next)
    })
    .then(success(res, 201))
    .catch(next)
}
*/

export const create = ({ bodymen: { body } }, res, next) => {
  // body.coverImage = body.images[0];
  const base = _.endsWith(apiBaseUrl, '/') ? apiBaseUrl.substring(0, apiBaseUrl.length - 2) : apiBaseUrl
  body.qrCode = base + '/pois/qr/' + body.uniqueName
  let newPoi = {
    name: body.name,
    categories: body.categories,
    loc: body.loc,
    uniqueName: body.uniqueName,
    qrCode: base + '/pois/qr/' + body.uniqueName,
    audioguides: body.audioguides,
    description: body.description,
    coverImage: body.coverImage,
    images: body.images,
    year: body.year,
    creator: body.creator,
    status: body.status,
    schedule: body.schedule,
    price: body.price
  }
  console.log('Nuevo poi ' + JSON.stringify(newPoi))
  Poi.create(newPoi)
    .then(success(res, 201))
    .catch(next)
}

/** Show list of all pois */
export const index = ({ querymen: { query, select, cursor } }, res, next) => {
  const userLogged = res.req.user
  let lat
  let lng
  if (query['loc.coordinates']) {
    lat = query['loc.coordinates']['$near']['$geometry'].coordinates[0]
    lng = query['loc.coordinates']['$near']['$geometry'].coordinates[1]
  }
  Poi.count(query)
    .then(count => Poi.find(query, select, cursor).populate('categories', 'id name')
      .then((pois) => ({
        count,
        rows: pois.map((poi) => {
          if (query['loc.coordinates']) { poi.set('distance', distance(lat, lng, poi.loc.coordinates[0], poi.loc.coordinates[1])) } else { poi.set('distance', -1) }
          if (userLogged.favs.length != 0) {
            userLogged.favs.forEach(userFav => {
              if (_.isEqual(userFav.toString(), poi.id)) { poi.set('fav', true) } else { poi.set('fav', false) }
            })
          } else { poi.set('fav', false) }
          if (userLogged.visited.length != 0) {
            userLogged.visited.forEach(userVisited => {
              if (_.isEqual(userVisited.toString(), poi.id)) { poi.set('visited', true) } else { poi.set('visited', false) }
            })
          } else { poi.set('visited', false) }
          return poi
        })
      }))
    )
    .then(success(res))
    .catch(next)
}

/** Show one poi translated to user language */
export const showTranslated = ({ params, user }, res, next) => {
  console.log('Entramos en showTranslated')
  // console.log('ID: ' + params.id)
  let query = {
    id: (mongoose.Types.ObjectId(params.id))
    // ,
    // idUserLanguage: (mongoose.Types.ObjectId(params.idUserLanguage))
  }
  // Poi.findOne({ id: query.id, 'description.translations.language': query.idUserLanguage })
  Poi.findOne({ _id: query.id }).populate('categories', 'id name')
    .then(notFound(res))
    /* .then((poi) => {
      console.log(JSON.stringify(poi))
      return poi
    }) */
    // .then((poi) => poi ? poi.view(0) : null)
    .then((poi) => {
      // console.log(JSON.stringify(poi))
      let spanishTranslation = _.find(poi.description.translations, (element) => {
        return element.language.language == '5c5429bf77bca32e879271d6'
      })
      poi.description.translations = _.filter(poi.description.translations, (element) => {
        // console.log(JSON.stringify(element))
        return element.language.language == params.idUserLanguage
      })
      if (poi.description.translations.length == 0) {
        console.log('No hay ninguno, se añade en castellano')
        poi.description.translations.push(spanishTranslation)
      }

      poi.audioguides.translations = _.filter(poi.audioguides.translations, (element) => {
        return element.language.language == params.idUserLanguage
      })
      // console.log('Filtrada la audioguia')

      poi.set('fav', _.includes(user.favs, poi.id))
      poi.set('visited', _.includes(user.visited, poi.id))

      return poi
    })
    .then(success(res))
    .catch(next)
}

/**  Show one POI info. Without visit it */
export const show = ({ params }, res, next) =>
  Poi.findById(params.id).populate('categories', 'id name')
    .then(notFound(res))
    .then((poi) => poi ? poi.view(1) : null)
    .then(success(res))
    .catch(next)

/** Used to update a POI */
export const update = ({ bodymen: { body }, params }, res, next) =>
  Poi.findById(params.id)
    .then(notFound(res))
    .then((poi) => poi ? Object.assign(poi, body).save() : null)
    .then((poi) => poi ? poi.view(1) : null)
    .then(success(res))
    .catch(next)

/** Action done when admin deletes a POI */
export const destroy = ({ params }, res, next) =>
  Poi.findById(params.id)
    .then(notFound(res))
    .then((poi) => poi ? poi.remove() : null)
    .then(success(res, 204))
    .catch(next)

/** Actions when user ScanQR.
 *    If user didn't visit the POI, add to UserVisited
 *    If user visited all POIs of a Medal, add to him.
 *    Devuelve el ID del poi, para poder hacer una petición de traducción **/

// TODO: ¿Qué tal devolver un modelo en el que se sepa si el usuario ha conseguido un Badge?
// Se puede pintar de alguna manera (diálogo) o similar.
export const VisitPoi = ({ params, user }, res, next) =>
  // Poi.findById(params.id).populate('categories', 'id name')
  Poi.findOne({ uniqueName: params.uniqueName }).populate('categories', 'id name')
    .then(notFound(res))
    .then((poi) => poi ? poi.view(1) : null) // Este objeto POI no se usa en la APP, ya que se consume del servicio de obtención de un POI traducido
    .then((poi) => {
      let newBadges = []
      if (user.visited.indexOf(poi.id) == -1) {
        user.visited.push(poi.id)
        Badge.find()
          .then(badges => {
            badges.map(badge => { // Para saber qué badges son los nuevos, se podría añadir un segundo array que contenga los conseguidos en esta petición
              if (arrayContainsArray(user.visited, badge.pois)) { user.badges.push(badge.id) }
            })
            user.save()
            return badges
          })
        poi.firstVisited = true
      }
      let result = {
        poi: poi,
        newBadges: {
          count: newBadges.length,
          rows: newBadges
        }
      }
      return result
    })
    .then(success(res))
    .catch(next)

/*
 *  Genera el código Qr a partir del nombre único del POI
 */
export const serveQrAsImg = ({ params, user, query }, res, next) => {
  const base = _.endsWith(apiBaseUrl, '/') ? apiBaseUrl.substring(0, apiBaseUrl.length - 2) : apiBaseUrl
  const requestBaseUrl = base + '/pois/visit/'
  Poi.findOne({ uniqueName: params.uniqueName })
    .then((poi) => QRCode.toDataURL(requestBaseUrl + poi.uniqueName, {
      color: {
        dark: '#000', // Black dots
        light: '#0000' // Transparent background
      },
      width: query.size === undefined ? 1024 : query.size,
      height: query.size === undefined ? 1024 : query.size
    }))
    .then((data) => {
      // console.log(data)
      const img = Buffer.from(data.split(',')[1], 'base64')
      res.append('Content-Type', 'image/png')
      res.append('Content-Length', img.length)
      res.send(img)
    })
    .catch(next)
}
/*
  Comprueba si el uniqueName ya existe, para no insertarlo duplicado
  Devuelve 200 si existe; en este caso no se debería usar
  Devuelve 404 si no existe; en este caso sí se puede usar como uniqueName
*/
export const existsUniqueName = ({ params }, res, next) => {
  Poi.findOne({ uniqueName: params.uniqueName }, 'name uniqueName')
    .then(notFound(res))
    .then(success(res))
    .catch(next)
}

/*
  Permite añadir un POI como favorito para el usuario logueado
*/
export const savePoiAsFav = ({ params, user }, res, next) => {
  if (user.favs.indexOf(params.id) === -1) {
    user.favs.push(params.id)
    user.save()
  }
  res.status(200).json(user.view(true))
}

/*
  Permite añadir un POI como favorito para el usuario logueado
*/
export const deletePoiAsFav = ({ params, user }, res, next) => {
  const index = user.favs.indexOf(params.id)
  if (index !== -1) {
    user.favs.splice(index, 1)
    user.save()
  }
  res.status(200).json(user.view(true))
}

/*
  Permite obtener los POIs favoritos de un usuario
*/
export const getFavorites = ({ user }, res, next) => {
  const userLogged = res.req.user
  Poi.count({_id: { $in: userLogged.favs }})
    .then(count => Poi.find({_id: { $in: userLogged.favs }}).populate('categories', 'id name')
      .then((pois) => ({
        count,
        rows: pois.map((poi) => {
          poi.set('fav', true)
          if (userLogged.visited.length != 0) {
            userLogged.visited.forEach(userVisited => {
              if (_.isEqual(userVisited.toString(), poi.id)) { poi.set('visited', true) } else { poi.set('visited', false) }
            })
          } else { poi.set('visited', false) }
          return poi
        })
      }))
    )
    .then(success(res))
    .catch(next)
}

/*
  Permite obtener los POIs visitados por un usuario
*/
export const getVisited = ({ user }, res, next) => {
  const userLogged = res.req.user
  Poi.count({_id: { $in: userLogged.visited }})
    .then(count => Poi.find({_id: { $in: userLogged.visited }}).populate('categories', 'id name')
      .then((pois) => ({
        count,
        rows: pois.map((poi) => {
          if (userLogged.favs.length != 0) {
            userLogged.favs.forEach(userFav => {
              if (_.isEqual(userFav.toString(), poi.id)) { poi.set('fav', true) } else { poi.set('fav', false) }
            })
          } else { poi.set('fav', false) }
          poi.set('visited', true)
          return poi
        })
      }))
    )
    .then(success(res))
    .catch(next)
}

/** Function used to calculate the distance between 2 POIs */
function distance (lat1, lon1, lat2, lon2) {
  var p = Math.PI / 180
  var c = Math.cos
  var a = 0.5 - c((lat2 - lat1) * p) / 2 + c(lat1 * p) * c(lat2 * p) * (1 - c((lon2 - lon1) * p)) / 2

  return 12742 * Math.asin(Math.sqrt(a)) * Math.PI * 360 // 2 * R; R = 6371 km
}

/** Function used to compare if one array contains another */
function arrayContainsArray (container, contained) {
  if (contained.length !== 0 && container.length !== 0) { return contained.every((value) => (container.indexOf(value) >= 0)) }
  return false
}
