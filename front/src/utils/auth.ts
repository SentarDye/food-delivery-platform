export function getToken(key: string = 'food_token'): string | null {
    return localStorage.getItem(key)
}
export function setToken(key: string, token: string): void {
    localStorage.setItem(key, token)
}
export function removeToken(key: string = 'food_token'): void {
    localStorage.removeItem(key)
}