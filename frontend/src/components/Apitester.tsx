import React, { useEffect, useState } from 'react';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { darcula } from 'react-syntax-highlighter/dist/esm/styles/prism';
import axios from 'axios';
import { ApiError } from '../model/models';


const App: React.FC = () => {
  const [response, setResponse] = useState<string>();
  const [responseMeta, setResponseMeta] = useState<any>();
  const [error, setError] = useState<string>('');
  const [endpoint, setEndpoint] = useState<string>('endpoint');
  const [searchName, setSearchName] = useState<string>('');
  const [language, setLanguage] = useState<string>('json');
  const [file, setFile] = useState<File>();

  const axiosInst = axios.create({
    baseURL: 'http://localhost:8080/api/v1',
  });
  
  axiosInst.interceptors.response.use(
    (response: any) => {
      setResponseMeta(response.data);
      return response;
    },
    (error: any) => {
      setResponseMeta(error.response.data);
      // Handle error response
      if (error.response) {
        const errorData: ApiError = error.response.data;
        setError(`${errorData.status} ${errorData.error} - ${errorData.message}`);
      } 
      else {
        setError(error.message);
      }
      return Promise.reject(error);
    }
  );

  useEffect(() => {
    setError('');
  }, [response]);

  useEffect(() => {
    setResponse('');
  }, [error])
  
  const executeReadFile = async () => {
    try {
      const res = await axiosInst.get('/products/read-file');
      setEndpoint('api/v1/products/read-file');
      setLanguage('txt');
      setResponse(JSON.stringify(res.data.message, null, 2));
    } 
    catch (err: any) {}
  };

  const executeGetAllProducts = async () => {
    try {
      const res = await axiosInst.get('/products/all');
      setEndpoint('api/v1/products/all');
      setLanguage('json');
      setResponse(JSON.stringify(res.data.data, null, 2));
    } 
    catch (err: any) {}
  };

  const executeGetProductsByName = async () => {
    try {
      const res = await axiosInst.get(`/products/${searchName}`);
      setEndpoint('api/v1/products/{name}');
      setLanguage('json');
      setResponse(JSON.stringify(res.data.data, null, 2));
    } 
    catch (err: any) {}
  };

  const executeGetXmlFileContent = async () => {
    try {
      const res = await axiosInst.get('/products/xml');
      setEndpoint('api/v1/products/xml');
      setLanguage('xml');
      setResponse(res.data);
    } 
    catch (err: any) {
    }
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const uploadedFile = event.target.files?.[0];
    if (uploadedFile) {
      setFile(uploadedFile);
    }
  };

  const executeUpdateXmlFile = async () => {
    try {
      const formData = new FormData();
      if (file) {
        formData.append('file', file);
        await axiosInst.put('/products/update-file', formData, {
            headers: { 'Content-Type': 'application/xml' },
        });
        setEndpoint('api/v1/products/update-file');
        setLanguage('txt');
        setResponse('File successfully updated.');
      } 
      else {
        setError('Please select a file.');
      }
    } 
    catch (err: any) {}
  };

  return (
    <div className="flex h-full">
      
      <div className="w-2/5 bg-neutral-100 p-5 space-y-7 text-md">
        <div className='bg-white p-3 rounded-sm'>
          <p>Read File and Get Products Length</p>
          <button
            className="w-full bg-blue-500 text-white py-2 px-4 rounded font-semibold mt-2 hover:bg-blue-600 text-lg"
            onClick={executeReadFile}
          >
            api/v1/products/read-file
          </button>
        </div>
        <div className='bg-white p-3 rounded-sm'>
          <p>Get All Products</p>
          <button
            className="w-full bg-blue-500 text-white py-2 px-4 rounded font-semibold mt-2 hover:bg-blue-600 text-lg"
            onClick={executeGetAllProducts}
          >
            api/v1/products/all
          </button>
        </div>
        <div className='bg-white p-3 rounded-sm'>
          <p>Get Products by Name</p>
          <input
            type="text"
            placeholder="Enter name"
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
            className="w-full p-2 border rounded mb-2 mt-2"
          />
          <button
            className="w-full bg-blue-500 text-white py-2 px-4 rounded font-semibold hover:bg-blue-600 text-lg"
            onClick={executeGetProductsByName}
          >
            api/v1/products/&#123;name&#125;
          </button>
        </div>
        <div className='bg-white p-3 rounded-sm'>
          <p>Get XML File Content</p>
          <button
            className="w-full bg-blue-500 text-white py-2 px-4 rounded font-semibold mt-2 hover:bg-blue-600 text-lg"
            onClick={executeGetXmlFileContent}
          >
            api/v1/products/xml
          </button>
        </div>
        <div className='bg-white p-3 rounded-sm'>
          <p>Update XML File</p>
          <input
            type="file"
            onChange={handleFileChange}
            className="w-full p-2 border rounded"
          />
          <button
            className="w-full bg-blue-500 text-white py-2 px-4 rounded font-semibold mt-2 hover:bg-blue-600 text-lg"
            onClick={executeUpdateXmlFile}
          >
            api/v1/products/update-file
          </button>
        </div>
      </div>

      <div className="w-3/4 p-4 bg-gray-200 response-box overflow-auto">
        <h2 className="text-2xl font-bold mb-5 border-solid  w-fit bg-slate-300 p-3 rounded-md">{endpoint}</h2>

        <div className='font-semibold text-lg w-fit bg-white rounded-md p-3 mb-3'>
          {responseMeta && (
            <React.Fragment>
              <p>Timestamp: {responseMeta.timeStamp?.toString()}</p>
              <p>Status: {responseMeta.status}</p>
              {responseMeta.error && <p>Message: {responseMeta.error}</p>}
              {responseMeta.message && <p>Message: {responseMeta.message}</p>}
            </React.Fragment>
          )}
        </div>

        {response && (
          <SyntaxHighlighter language={language} style={darcula} showLineNumbers={true} customStyle={{ borderRadius: '5px'}}>
            {response}
          </SyntaxHighlighter>
        )}
        {error !== '' && (
          <div className="text-red-500 mt-5 font-semibold text-lg">
            {error}
          </div>
        )}
      </div>
      
    </div>
  );
};

export default App;
