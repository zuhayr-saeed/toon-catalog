import React, { useState } from 'react';

const ApiTest: React.FC = () => {
  const [result, setResult] = useState<string>('Not tested yet');
  const [loading, setLoading] = useState(false);

  const testBackendConnection = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/v1/healthz');
      if (response.ok) {
        const data = await response.text();
        setResult(`✅ Backend connected! Health check: ${data}`);
      } else {
        setResult(`❌ Backend responded with status: ${response.status}`);
      }
    } catch (error: any) {
      setResult(`❌ Connection failed: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };


  const testStoriesEndpoint = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/v1/series?page=0&size=5');
      console.log('Stories response status:', response.status);
      console.log('Stories response headers:', response.headers);
      
      const responseText = await response.text();
      console.log('Stories raw response:', responseText);
      
      if (response.ok) {
        try {
          const data = JSON.parse(responseText);
          setResult(`✅ Stories endpoint works! Found ${data.totalElements || data.length || 'unknown'} stories`);
        } catch (parseError) {
          setResult(`❌ Stories endpoint returned non-JSON: ${responseText.substring(0, 200)}...`);
        }
      } else {
        setResult(`❌ Stories endpoint failed with status: ${response.status}. Response: ${responseText.substring(0, 200)}`);
      }
    } catch (error: any) {
      setResult(`❌ Stories request failed: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const testAvailableEndpoints = async () => {
    setLoading(true);
    const endpoints = [
      '/api/v1/series',
      '/api/v1/serie', // Maybe it's singular?
      '/api/series',  // Maybe no v1?
      '/series',      // Maybe no api prefix?
    ];
    
    let results = [];
    
    for (const endpoint of endpoints) {
      try {
        const response = await fetch(endpoint);
        results.push(`${endpoint}: ${response.status} ${response.statusText}`);
      } catch (error: any) {
        results.push(`${endpoint}: ERROR - ${error.message}`);
      }
    }
    
    setResult(`🔍 Endpoint scan results:\n${results.join('\n')}`);
    setLoading(false);
  };

  return (
    <div style={{ 
      border: '1px solid #ccc', 
      padding: '20px', 
      margin: '20px 0',
      borderRadius: '8px'
    }}>
      <h3>🔧 API Connection Test</h3>
      
      <div style={{ marginBottom: '15px' }}>
        <button 
          onClick={testBackendConnection}
          disabled={loading}
          style={{ 
            padding: '10px 15px', 
            marginRight: '10px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Testing...' : 'Test Health Check'}
        </button>
        
        <button 
          onClick={testStoriesEndpoint}
          disabled={loading}
          style={{ 
            padding: '10px 15px',
            marginRight: '10px',
            backgroundColor: '#28a745',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Testing...' : 'Test Stories API'}
        </button>

        <button 
          onClick={testAvailableEndpoints}
          disabled={loading}
          style={{ 
            padding: '10px 15px',
            backgroundColor: '#ffc107',
            color: 'black',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Testing...' : 'Scan Endpoints'}
        </button>
      </div>
      
      <div style={{ 
        backgroundColor: '#f8f9fa', 
        padding: '10px', 
        borderRadius: '4px',
        fontFamily: 'monospace',
        whiteSpace: 'pre-wrap',
        maxHeight: '200px',
        overflow: 'auto'
      }}>
        <strong>Result:</strong> {result}
      </div>
    </div>
  );
};

export default ApiTest;
