export interface LoginResponse {
  token: string;
  user: {
    _id: string;
    name: string;
    email: string;
    role: string;
    picture: string;
    language: string;
  };

}
