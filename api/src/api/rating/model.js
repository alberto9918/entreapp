import mongoose, { Schema } from 'mongoose'

const ratingSchema = new Schema({
  user: {
    type: Schema.ObjectId,
    ref: 'User',
    required: true
  },
  rating: {
    type: Number
  },
  poi: {
    type: Schema.Types.ObjectId,
    ref: 'Poi',
    required: true
  },
}, {
  timestamps: true,
  toJSON: {
    virtuals: true,
    transform: (obj, ret) => { delete ret._id }
  }
})

ratingSchema.methods = {
  view (full) {
    const view = {
      // simple view
      id: this.id,
      user: this.user,
      rating: this.rating,
      poi: this.poi,
      createdAt: this.createdAt,
      updatedAt: this.updatedAt
    }

    return full ? {
      ...view
      // add properties for a full view
    } : view
  }
}

const model = mongoose.model('Rating', ratingSchema)

export const schema = model.schema
export default model
