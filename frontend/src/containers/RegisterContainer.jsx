import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import apiService from '../api/apiService';

const registerSchema = z.object({
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
  email: z.string().email('Invalid email format').min(1, 'Email is required'),
  phone: z.string().regex(/^[6-9]\d{9}$/, 'Invalid Indian phone number').min(1, 'Phone is required'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  role: z.enum(['GUEST', 'HOST'], { required_error: 'Role is required' }),
});

export default function RegisterContainer() {
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data) => {
    try {
      const response = await apiService.register(data);
      if (response.success) {
         navigate('/login');
      }
    } catch (error) {
      console.error('Registration failed', error);
    }
  };

  return (
    <div>
      <h2>Register</h2>
      <form onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label>First Name</label>
          <input type="text" {...register('firstName')} />
          {errors.firstName && <p>{errors.firstName.message}</p>}
        </div>
        <div>
          <label>Last Name</label>
          <input type="text" {...register('lastName')} />
          {errors.lastName && <p>{errors.lastName.message}</p>}
        </div>
        <div>
          <label>Email</label>
          <input type="email" {...register('email')} />
          {errors.email && <p>{errors.email.message}</p>}
        </div>
        <div>
          <label>Phone</label>
          <input type="text" {...register('phone')} />
          {errors.phone && <p>{errors.phone.message}</p>}
        </div>
        <div>
          <label>Password</label>
          <input type="password" {...register('password')} />
          {errors.password && <p>{errors.password.message}</p>}
        </div>
        <div>
          <label>Role</label>
          <select {...register('role')}>
            <option value="GUEST">Guest</option>
            <option value="HOST">Host</option>
          </select>
          {errors.role && <p>{errors.role.message}</p>}
        </div>
        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Registering...' : 'Register'}
        </button>
      </form>
    </div>
  );
}
