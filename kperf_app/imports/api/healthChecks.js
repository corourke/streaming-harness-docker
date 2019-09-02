import { Mongo } from 'meteor/mongo';

export const HealthChecks = new Mongo.Collection('healthChecks');
