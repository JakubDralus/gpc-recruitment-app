import React from 'react';

interface ResponseMetaProps {
  responseMeta: any;
}

const ResponseMeta: React.FC<ResponseMetaProps> = ({ responseMeta }) => {
  return (
    <div className='font-semibold text-lg bg-white rounded-md p-3 mb-5'>
      {responseMeta && (
        <React.Fragment>
          <p>Timestamp: {responseMeta.timeStamp?.toString()}</p>
          <p>Status: {responseMeta.status}</p>
          {responseMeta.error && <p>Message: {responseMeta.error}</p>}
          {responseMeta.message && <p>Message: {responseMeta.message}</p>}
        </React.Fragment>
      )}
    </div>
  );
};

export default ResponseMeta;
