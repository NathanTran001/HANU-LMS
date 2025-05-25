import React, { useState } from 'react';
import TextField from './TextField';

export default function MyForm() {
  const [name, setName] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Form submitted with name:", name);
  };

  return (
    <form onSubmit={handleSubmit}>
      <TextField
        label="Your Name"
        placeholder="Enter your name"
        value={name}
        onChange={setName}
        required={true}
      />
      <button type="submit">Submit</button>
    </form>
  );
}
