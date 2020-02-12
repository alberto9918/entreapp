import { Rating } from '.'
import { User } from '../user'

let user, rating

beforeEach(async () => {
  user = await User.create({ email: 'a@a.com', password: '123456' })
  rating = await Rating.create({ user, rating: 'test', poi: 'test' })
})

describe('view', () => {
  it('returns simple view', () => {
    const view = rating.view()
    expect(typeof view).toBe('object')
    expect(view.id).toBe(rating.id)
    expect(typeof view.user).toBe('object')
    expect(view.user.id).toBe(user.id)
    expect(view.rating).toBe(rating.rating)
    expect(view.poi).toBe(rating.poi)
    expect(view.createdAt).toBeTruthy()
    expect(view.updatedAt).toBeTruthy()
  })

  it('returns full view', () => {
    const view = rating.view(true)
    expect(typeof view).toBe('object')
    expect(view.id).toBe(rating.id)
    expect(typeof view.user).toBe('object')
    expect(view.user.id).toBe(user.id)
    expect(view.rating).toBe(rating.rating)
    expect(view.poi).toBe(rating.poi)
    expect(view.createdAt).toBeTruthy()
    expect(view.updatedAt).toBeTruthy()
  })
})
