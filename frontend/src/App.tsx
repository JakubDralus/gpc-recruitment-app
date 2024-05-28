import ApiTester from './components/Apitester';

function App() {
  return (
    <div className="mycontainer h-screen flex flex-col">
      <header className="bg-gray-700 text-white p-4 header">
        <h1 className="text-3xl font-bold">GPC Recruitment App API</h1>
      </header>
      <main className="flex-1 overflow-hidden">
        <ApiTester />
      </main>
    </div>
  );
}

export default App;
