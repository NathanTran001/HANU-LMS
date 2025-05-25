import React from 'react';

const TextField = ({
  label,
  icon,
  placeholder = '',
  required = false,
  value,
  onChange,
}) => {
  return (
    <div style={styles.textfieldContainer}>
      <label style={styles.textfieldLabel}>
        {label}
        {!required && <span style={styles.textfieldRequired}>*</span>}
      </label>
      <div style={styles.textfieldInputWrapper}>
        {icon && (
          <div style={styles.textfieldIconBox}>
            <img src={icon} alt="icon" style={styles.textfieldIcon} />
          </div>
        )}
        <input
          type="text"
          placeholder={placeholder}
          required={required}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          style={styles.textfieldInput}
        />
      </div>
    </div>
  );
};

const styles = {
  textfieldContainer: {
    display: 'flex',
    flexDirection: 'column',
},
  textfieldLabel: {
    fontSize: '14px',
    fontWeight: 500,
    marginBottom: '4px',
    color: '#333',
    textAlign: 'left',
  },
  textfieldRequired: {
    color: 'red',
    marginLeft: '4px',
  },
  textfieldInputWrapper: {
    display: 'flex',
    flex: 1,
    alignItems: 'center',
    border: '1px solid #ccc',
    borderRadius: '4px',
    overflow: 'hidden',
  },
  textfieldIconBox: {
    backgroundColor: '#e0e0e0',
    padding: '8px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  textfieldIcon: {
    width: '16px',
    height: '16px',
  },
  textfieldInput: {
    flex: 1,
    border: 'none',
    padding: '8px',
    fontSize: '14px',
    outline: 'none',
  },
};

export default TextField;
