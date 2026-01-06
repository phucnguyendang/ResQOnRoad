import { render, screen } from '@testing-library/react';
import App from './App.jsx';

test('renders app brand', () => {
  render(<App />);
  // Brand appears in multiple places (e.g., header + footer).
  expect(screen.getAllByText('ResQOnRoad').length).toBeGreaterThan(0);
});
