import crypto from 'crypto'
import bcrypt from 'bcryptjs'
import randtoken from 'rand-token'
import mongoose, { Schema, SchemaType } from 'mongoose'
import mongooseKeywords from 'mongoose-keywords'
import { env } from '../../config'

export const roles = ['user', 'admin', 'contributor']

const userSchema = new Schema({
  email: {
    type: String,
    match: /^\S+@\S+\.\S+$/,
    required: true,
    unique: true,
    trim: true,
    lowercase: true
  },
  password: {
    type: String,
    required: true,
    minlength: 6
  },
  name: {
    type: String,
    index: true,
    trim: true
  },
  services: {
    facebook: String,
    google: String
  },
  role: {
    type: String,
    enum: roles,
    default: 'user'
  },
  picture: {
    type: String,
    trim: true
  },
  images: [{
    poi: {
      type: Schema.Types.ObjectId,
      ref: 'Poi'
    },
    thumbnail: {
      type: String
    },
    full: {
      type: String
    }
  }],
  invalidImages: [{
    poi: {
      type: Schema.Types.ObjectId,
      ref: 'Poi'
    },
    thumbnail: {
      type: String
    },
    full: {
      type: String
    },
    dateToBeRemoved: {
      type: Date
    }
  }],
  likes: [{
    type: Schema.Types.ObjectId,
    ref: 'Category'
  }],
  visited: [{
    type: Schema.Types.ObjectId,
    ref: 'Poi'
  }],
  favs: [{
    type: Schema.Types.ObjectId,
    ref: 'Poi'
  }],
  badges: [{
    type: Schema.Types.ObjectId,
    ref: 'Badge'
  }],
  city: {
    type: String
  },
  language: {
    type: Schema.Types.ObjectId,
    ref: 'Language'
  },
  friends: [{
    type: Schema.Types.ObjectId,
    ref: 'User'
  }]
}, {
  strict: false,
  timestamps: true
})

userSchema.path('email').set(function (email) {
  if (!this.picture || this.picture.indexOf('https://gravatar.com') === 0) {
    const hash = crypto.createHash('md5').update(email).digest('hex')
    this.picture = `https://gravatar.com/avatar/${hash}?d=identicon`
  }

  if (!this.name) {
    this.name = email.replace(/^(.+)@.+$/, '$1')
  }

  return email
})

userSchema.pre('save', function (next) {
  if (!this.isModified('password')) return next()

  /* istanbul ignore next */
  const rounds = env === 'test' ? 1 : 9

  bcrypt.hash(this.password, rounds).then((hash) => {
    this.password = hash
    next()
  }).catch(next)
})

userSchema.methods = {
  view (full) {
    let view = {
      id: this.id,
      name: this.name,
      picture: this.picture,
      images: this.images,
      invalidImages: this.invalidImages,
      badges: this.badges,
      role: this.role,
      email: this.email,
      city: this.city,
      likes: this.likes,
      favs: this.favs,
      visited: this.visited,
      language: this.language,
      friends: this.friends,
      createdAt: this.createdAt,
      updatedAt: this.updatedAt
    }

    return full ? {
      ...view
      // add properties for a full view
    } : view
  },

  authenticate (password) {
    return bcrypt.compare(password, this.password).then((valid) => valid ? this : false)
  }
}

userSchema.statics = {
  roles,

  createFromService ({ service, id, email, name, picture }) {
    return this.findOne({ $or: [{ [`services.${service}`]: id }, { email }] }).then((user) => {
      if (user) {
        user.services[service] = id
        user.name = name
        user.picture = picture
        return user.save()
      } else {
        const password = randtoken.generate(16)
        return this.create({ services: { [service]: id }, email, password, name, picture })
      }
    })
  }
}

userSchema.plugin(mongooseKeywords, { paths: ['email', 'name'] })

const model = mongoose.model('User', userSchema)

export const schema = model.schema
export default model
