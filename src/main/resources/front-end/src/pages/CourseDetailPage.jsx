import React, { useState } from 'react';
import TextField from '../components/TextField';

export default function CourseDetailPage() {
  const [name, setName] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Form submitted with name:", name);
  };

  return (
    <div>
        CourseDetailPage
    </div>
  );
}
