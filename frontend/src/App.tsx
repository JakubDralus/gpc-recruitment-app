import ApiTester from './components/Apitester';

function App() {

  const handleRedirect = () => {
    window.open('http://localhost:8080/swagger-ui/index.html', '_blank');
  };

  return (
    <div className="mycontainer h-screen flex flex-col">
      <header className="bg-gray-700 text-white p-4 header flex justify-between">
        <h1 className="text-3xl font-semibold w-fit">GPC Recruitment App API playground</h1>
        <button className='float float-end text-3xl underline'
          onClick={handleRedirect}
        >
          Swagger Docs
        </button>
      </header>
      <main className="flex-1 overflow-hidden">
        <ApiTester />
      </main>
    </div>
  );
}

export default App;
