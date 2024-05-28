
export interface ApiResponse<T> {
  timeStamp: Date;
  status: number;
  message: string;
  data?: T;
}

export interface ApiError {
  timeStamp: Date;
  status: number;
  error: string;
  message: string;
  path: string;
}

export interface ProductDTO {
  id: number;
  name: string;
  category: string;
  partNumberNR: string;
  companyName: string;
  active: boolean;
}