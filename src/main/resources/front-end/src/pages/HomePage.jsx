import React, { useState } from 'react';
import TextField from '../components/TextField';
import { Link } from 'react-router-dom';

export default function HomePage() {
  const [facultyCode, setFacultyCode] = useState('');
  const [facultyName, setFacultyName] = useState('');

const handleSubmit = async (e) => {
  e.preventDefault();

  try {
    const response = await fetch('http://localhost:8081/admin/createFaculty', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        facultyCode,
        facultyName,
      }),
    });

    const data = await response.json();
    console.log('Server response:', data);
  } catch (error) {
    console.error('Error submitting form:', error);
  }
};

  return (
    <div>
      <div>HomePage</div>
      <Link to={'/courseDetail'}>Course Detail</Link>
      <form onSubmit={handleSubmit}>
        <TextField
          label="Faculty Code"
          placeholder="Enter Faculty Code"
          value={facultyCode}
          onChange={setFacultyCode}
          required={true}
        />
        <TextField
          label="Faculty Name"
          placeholder="Enter Faculty Name"
          value={facultyName}
          onChange={setFacultyName}
          required={true}
        />
        <button type="submit">Submit</button>
      </form>
    </div>
  );
}
