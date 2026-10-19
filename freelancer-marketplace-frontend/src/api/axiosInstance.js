import axios from "axios";
const api=axios.create({baseURL:import.meta.env.VITE_API_URL||"http://localhost:8081/api",headers:{"Content-Type":"application/json"}});
api.interceptors.request.use(config=>{const token=localStorage.getItem("token");if(token)config.headers.Authorization=`Bearer ${token}`;return config;});
export function message(error){return error?.response?.data?.message||error?.response?.data||error?.message||"Something went wrong.";}
export function claims(token){try{return JSON.parse(atob(token.split(".")[1].replace(/-/g,"+").replace(/_/g,"/")));}catch{return {};}}
export default api;